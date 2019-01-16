<style lang="less">
</style>
<template>
  <div>
    <Row>
      <Col>
        <Card>
          <Tabs value="1">
            <TabPane label="表格树组件" name="1">
              <Alert type="info" show-icon>说明：基于iView树组件扩展，无需使用Render函数，使用详见该组件示例代码</Alert>
              <XbootTreeTable show-checkbox :tree-data="treeData">
                <XbootTreeTableColumn prop="name" label="姓名" width="200px"></XbootTreeTableColumn>
                <XbootTreeTableColumn prop="email" label="邮箱" width="200px"></XbootTreeTableColumn>
                <XbootTreeTableColumn prop="icon" label="图标" width="200px">
                  <template slot-scope="scope">
                    <Icon :type="scope.node.data.icon" size="20" :color="scope.node.data.color"/>
                  </template>
                </XbootTreeTableColumn>
                <XbootTreeTableColumn prop="status" label="状态" width="200px">
                  <template slot-scope="scope">
                    <i-switch
                      size="large"
                      @on-change="changeStatus(scope.node.data)"
                      v-model="scope.node.data.status"
                    >
                      <span slot="open">开启</span>
                      <span slot="close">关闭</span>
                    </i-switch>
                  </template>
                </XbootTreeTableColumn>
                <XbootTreeTableColumn label="操作" width="200px">
                  <template slot-scope="scope">
                    <Button
                      @click="edit(scope.node.data)"
                      type="primary"
                      size="small"
                      style="margin-right:5px"
                    >编辑</Button>
                    <Button @click="del(scope.node.data)" type="error" size="small">删除</Button>
                  </template>
                </XbootTreeTableColumn>
              </XbootTreeTable>
            </TabPane>
            <TabPane label="示例模板" name="2">待开发</TabPane>
          </Tabs>
        </Card>
      </Col>
    </Row>
  </div>
</template>

<script>
import XbootTreeTable from "../../my-components/xboot-tree-table/xboot-tree-table";
import XbootTreeTableColumn from "../../my-components/xboot-tree-table/xboot-tree-table-column";
export default {
  name: "tree-table",
  components: {
    XbootTreeTable,
    XbootTreeTableColumn
  },
  data() {
    return {
      treeData: [
        {
          data: {
            id: 1,
            name: "Exrick",
            email: "1012139570@qq.com",
            icon: "logo-xbox",
            color: "#55b71f",
            status: true,
          },
          children: [
            {
              data: {
                id: 2,
                name: "XBoot",
                email: "1012139570@qq.com",
                icon: "logo-apple",
                color: "#c8c8c8",
                status: true,
              },
              children: [
                {
                  data: {
                    id: 3,
                    name: "前后端分离",
                    email: "1012139570@qq.com",
                    icon: "logo-chrome",
                    color: "#1fb1da",
                    status: true,
                  }
                },
                {
                  data: {
                    id: 4,
                    name: "开发平台",
                    email: "1012139570@qq.com",
                    icon: "logo-github",
                    color: "#181617",
                    status: false,
                  }
                }
              ]
            }
          ]
        }
      ]
    };
  },
  methods: {
    changeStatus(data) {
      this.$Message.info(`修改了${data.name}的状态为${data.status}`);
    },
    edit(v) {
      this.$Message.info(`点击了${v.name}的编辑`);
    },
    del(v) {
      this.$Message.info(`点击了${v.name}的删除`);
    }
  },
  mounted() {}
};
</script>