<template>
  <div></div>
</template>

<script>
  export default {
    name: 'xboot-tree-table-column',
    props: {
      prop: String,
      label: String,
      width: String
    },
    methods: {
      renderCell (h, {node}, width) {
        let text = '';
        if (this.$scopedSlots.default) {
          text = this.$scopedSlots.default(node);
        } else if (this.prop) {
          text = node.node.data[this.prop] || '';
        }

        return h('span', {
          'class': 'xboot-tree-table__td',
          style: {
            width: width
          }
        }, [h('span', null, text)]);
      }
    },
    created () {
      // 在父组件记录列组件实例
      this.$parent.columns.push(this);
    }
  };
</script>

<style>
  .xboot-tree-table__td {
    color: #515a6e;
    font-size: 12px;
    height: 40px;
    line-height: 40px;
    border-right: 1px solid #dcdfe6;
    text-align: start;
    padding-left: 16px;
  }
</style>
