<template>
  <div>
    <xboot-tree-table-header :columns="columns" :width-array="headerWidthArray"></xboot-tree-table-header>
    <Tree
      :show-checkbox="showCheckbox"
      multiple
      :empty-text="emptyText"
      :children-key="childrenKey"
      :data="treeData"
      :render="renderContent"
      :style="{width:treeWidth}"
    ></Tree>
    <div style="display: none;" :class="`__hidden`">
      <slot></slot>
    </div>
  </div>
</template>

<script>
import XbootTreeTableHeader from "./xboot-tree-table-header";
import { Tree } from "iview";
import "./iview-reset.less";

const BORDER_WIDTH = 1;
const ARROW_WIDTH = 24;
const CHECKBOX_WIDTH = 22;
const INDENT_DEFAULT = 18;
const EVENTS = [
  "on-select-change",
  "on-check-change",
  "on-toggle-expand"
];
export default {
  name: "xboot-tree-table",
  components: { XbootTreeTableHeader },
  props: {
    ...Tree.props,
    treeWidth: { type: String, default: "100%" },
    treeData: Array
  },
  data() {
    return {
      columns: [],
      widthArray: []
    };
  },
  mounted() {
    this.getColumnLevel(this.treeData);
  },
  computed: {
    headerWidthArray() {
      // 没有设置宽度的列总数
      const noWidthCount = this.columns.filter(n => !n.width).length;
      // 已分配了多少像素宽度
      let assignedWidthPx = 0;
      // 已分配了百分比宽度
      let assignedWidthPercent = 0;
      // 计算已分配了多少宽度
      this.columns.forEach(n => {
        if (n.width) {
          if (typeof n.width === "number" || n.width.includes("px")) {
            assignedWidthPx += Number.parseInt(n.width) + BORDER_WIDTH;
          } else if (n.width.includes("%")) {
            assignedWidthPercent += Number.parseInt(n.width) + BORDER_WIDTH;
          }
        }
      });
      // 是否有checkbox的偏移值
      let diff = CHECKBOX_WIDTH;
      // 有checkbox，加上checkbox占用的宽度
      if (this.showCheckbox) {
        assignedWidthPx += CHECKBOX_WIDTH + ARROW_WIDTH;
      } else {
        assignedWidthPx += ARROW_WIDTH;
        diff = 0;
      }
      // 计算每列应该展示的宽度
      return this.columns.map((column, index) => {
        // 有制定宽度
        if (column.width) {
          if (index === 0) {
            // 第一列需要加上箭头和checkbox占用的宽度
            return `calc(${column.width} + ${diff + ARROW_WIDTH}px)`;
          } else {
            // 最后一列需要减去每列的边框线宽度
            if (index === this.columns.length - 1) {
              return `calc(${column.width})`;
            } else {
              return `calc(${column.width})`;
            }
          }
        } else {
          if (index === 0) {
            // 第一列需要加上箭头和checkbox占用的宽度
            return `calc((100% - ${assignedWidthPercent}% - ${assignedWidthPx}px) / ${noWidthCount} + ${diff +
              ARROW_WIDTH}px)`;
          } else {
            if (index === this.columns.length - 1) {
              // 最后一列需要减去每列的边框线宽度
              return `calc((100% - ${assignedWidthPercent}% - ${assignedWidthPx}px) / ${noWidthCount} - ${
                this.columns.length
              }px)`;
            } else {
              return `calc((100% - ${assignedWidthPercent}% - ${assignedWidthPx}px) / ${noWidthCount})`;
            }
          }
        }
      });
    }
  },
  methods: {
    getColumnWidthArray(level) {
      // 没有设置宽度的列总数
      const noWidthCount = this.columns.filter(n => !n.width).length;
      // 已分配了多少像素宽度
      let assignedWidthPx = 0;
      // 已分配了百分比宽度
      let assignedWidthPercent = 0;

      // 当前层级缩进的宽度
      let indent = (level - 1) * (this.indent || INDENT_DEFAULT);

      // 计算已分配了多少宽度
      this.columns.forEach(n => {
        if (n.width) {
          if (typeof n.width === "number" || n.width.includes("px")) {
            assignedWidthPx += Number.parseInt(n.width) + BORDER_WIDTH;
          } else if (n.width.includes("%")) {
            assignedWidthPercent += Number.parseInt(n.width) + BORDER_WIDTH;
          }
        }
      });
      // 计算每列应该展示的宽度
      return this.columns.map((column, index) => {
        // 如果属性指定了宽度
        if (column.width) {
          // 第一列需要减去树层级缩进的宽度
          if (index === 0) {
            return `calc(${column.width} - ${indent + 6}px)`;
          } else {
            return column.width;
          }
        } else {
          // 如果列没有制定宽度，没宽度的列平均分配剩余的宽度，第一列需要减去树层级缩进的宽度
          if (index === 0) {
            return `calc((100% - ${assignedWidthPercent}% - ${assignedWidthPx -
              indent}px) / ${noWidthCount} -  ${indent}px)`;
          } else {
            return `calc((100% - ${assignedWidthPercent}% - ${assignedWidthPx -
              indent}px) / ${noWidthCount})`;
          }
        }
      });
    },
    /**
     * 给树添加层级属性
     * @param {array} data 树数据
     * @param {number} level 层级
     */
    getColumnLevel(data, level = 0) {
      data.forEach(item => {
        if (Object.getOwnPropertyDescriptor(item, "children")) {
          this.getColumnLevel(item["children"], level + 1);
        }
        item.data["dep"] = level;
      });
    },
    renderContent(h, { node, data }) {
      let widthArray = this.getColumnWidthArray(data.data.dep);
      const columns = this.columns.map((column, index) => {
        return column.renderCell.call(
          this._renderProxy,
          h,
          { node },
          widthArray[index]
        );
      });
      return h(
        "span",
        {
          class: `xboot__tr`,
          style: {
            height: "40px",
            display: "flex"
          }
        },
        columns
      );
    }
  }
};
</script>

<style lang="less" scoped>
.xboot__tr {
  align-items: center;

  .xboot-tree-table__td:last-child {
    border-right: none;
  }
}
</style>
