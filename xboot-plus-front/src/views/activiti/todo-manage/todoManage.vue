<style lang="less">
@import "./todoManage.less";
</style>
<template>
  <div class="search">
    <Row>
      <Col>
        <Card>
          <Row>
            <Form ref="searchForm" :model="searchForm" inline :label-width="70" class="search-form">
              <Form-item label="任务名称" prop="name">
                <Input
                  type="text"
                  v-model="searchForm.name"
                  placeholder="请输入"
                  clearable
                  style="width: 200px"
                />
              </Form-item>
              <Form-item style="margin-left:-35px;" class="br">
                <Button @click="handleSearch" type="primary" icon="ios-search">搜索</Button>
                <Button @click="handleReset">重置</Button>
              </Form-item>
            </Form>
          </Row>
          <Row class="operation">
            <Button @click="getDataList" icon="md-refresh">刷新</Button>
            <circleLoading v-if="operationLoading"/>
          </Row>
          <Row>
            <Alert show-icon>
              已选择
              <span class="select-count">{{selectCount}}</span> 项
              <a class="select-clear" @click="clearSelectAll">清空</a>
            </Alert>
          </Row>
          <Row>
            <Table
              :loading="loading"
              border
              :columns="columns"
              sortable="custom"
              :data="data"
              @on-selection-change="showSelect"
              ref="table"
            ></Table>
          </Row>
          <Row type="flex" justify="end" class="page">
            <Page
              :current="searchForm.pageNumber"
              :total="total"
              :page-size="searchForm.pageSize"
              @on-change="changePage"
              @on-page-size-change="changePageSize"
              :page-size-opts="[10,20,50]"
              size="small"
              show-total
              show-elevator
              show-sizer
            ></Page>
          </Row>
        </Card>
      </Col>
    </Row>
    <!-- 审批操作 -->
    <Modal :title="modalTaskTitle" v-model="modalTaskVisible" :mask-closable="false" :width="500">
      <Form ref="form" :model="form" :label-width="75" :rules="formValidate">
        <FormItem label="审批意见" prop="reason">
          <Input type="textarea" v-model="form.comment" :rows="4"/>
        </FormItem>
        <FormItem
          label="下一审批人"
          prop="assignee"
          style="position:relative"
          v-if="showAssign"
          :error="error"
        >
          <Select
            v-model="form.assignee"
            placeholder="请选择或输入搜索"
            filterable
            clearable
            :loading="userLoading"
          >
            <Option v-for="(item, i) in assigneeList" :key="i" :value="item.id">{{item.username}}</Option>
          </Select>
        </FormItem>
        <FormItem label="选择委托人" prop="userId" :error="error" v-if="form.type==2">
          <Select
            v-model="form.userId"
            placeholder="请输入用户名搜索选择用户"
            filterable
            remote
            :remote-method="searchUser"
            :loading="userLoading"
          >
            <Option v-for="(item, i) in userList" :value="item.id" :key="i">{{item.username}}</Option>
          </Select>
        </FormItem>
      </Form>
      <div slot="footer">
        <Button type="text" @click="modalTaskVisible=false">取消</Button>
        <Button type="primary" :loading="submitLoading" @click="handelSubmit">提交</Button>
      </div>
    </Modal>

    <Modal :title="modalTitle" v-model="modalVisible" :mask-closable="false" :width="500">
      <Form ref="delForm" :model="delForm" :label-width="70">
        <FormItem label="删除原因" prop="reason">
          <Input type="textarea" v-model="delForm.reason" :rows="4"/>
        </FormItem>
      </Form>
      <div slot="footer">
        <Button type="text" @click="modalVisible=false">取消</Button>
        <Button type="primary" :loading="submitLoading" @click="handelSubmitDel">提交</Button>
      </div>
    </Modal>
  </div>
</template>

<script>
import {
  todoList,
  pass,
  back,
  delegate,
  getNextNode,
  deleteTask
} from "@/api/activiti";
import { searchUserByName, getDictDataByType } from "@/api/index";
import circleLoading from "../../my-components/circle-loading.vue";
export default {
  name: "todo-manage",
  components: {
    circleLoading
  },
  data() {
    return {
      loading: true, // 表单加载状态
      operationLoading: false, // 操作加载状态
      modalTaskVisible: false,
      userLoading: false,
      userList: [],
      selectCount: 0, // 多选计数
      selectList: [], // 多选数据
      assigneeList: [],
      error: "",
      showAssign: false,
      searchForm: {
        // 搜索框对应data对象
        name: "",
        pageNumber: 1, // 当前页数
        pageSize: 10 // 页面大小
      },
      modalType: 0, // 添加或编辑标识
      modalVisible: false, // 添加或编辑显示
      modalTaskTitle: "",
      modalTitle: "", // 添加或编辑标题
      form: {
        id: "",
        userId: "",
        procInstId: "",
        comment: "",
        type: 0
      },
      delForm: {
        // 添加或编辑表单对象初始化数据
        reason: ""
      },
      formValidate: {
        // 表单验证规则
      },
      submitLoading: false, // 添加或编辑提交状态
      columns: [
        // 表头
        {
          type: "selection",
          width: 60,
          align: "center"
        },
        {
          type: "index",
          width: 60,
          align: "center"
        },
        {
          title: "任务名称",
          key: "name",
          width: 160
        },
        {
          title: "所属流程",
          key: "processName",
          width: 150
        },
        {
          title: "委托代办人",
          key: "owner",
          width: 130
        },
        {
          title: "流程发起人",
          key: "applyer",
          width: 130
        },
        {
          title: "优先级",
          key: "priority",
          width: 100,
          sortable: true,
          render: (h, params) => {
            let text = "无",
              color = "";
            if (params.row.priority == 0) {
              color = "green";
            } else if (params.row.priority == 1) {
              color = "orange";
            } else if (params.row.priority == 2) {
              color = "red";
            } else {
              color = "default";
            }
            this.dictPriority.forEach(e => {
              if (e.value == params.row.priority) {
                text = e.title;
              }
            });
            return h("div", [
              h(
                "Tag",
                {
                  props: {
                    color: color
                  }
                },
                text
              )
            ]);
          }
        },
        {
          title: "状态",
          key: "isSuspended",
          align: "center",
          width: 130,
          render: (h, params) => {
            let re = "";
            if (!params.row.isSuspended) {
              return h("div", [
                h(
                  "Tag",
                  {
                    props: {
                      type: "dot",
                      color: "success"
                    }
                  },
                  "已激活"
                )
              ]);
            } else if (params.row.isSuspended) {
              return h("div", [
                h(
                  "Tag",
                  {
                    props: {
                      type: "dot",
                      color: "error"
                    }
                  },
                  "已挂起"
                )
              ]);
            }
          },
          filters: [
            {
              label: "已激活",
              value: false
            },
            {
              label: "已挂起",
              value: true
            }
          ],
          filterMultiple: false,
          filterMethod(value, row) {
            if (value) {
              return row.isSuspended === true;
            } else if (!value) {
              return row.isSuspended === false;
            }
          }
        },
        {
          title: "创建时间",
          key: "createTime",
          width: 150,
          sortType: "desc",
          sortable: true
        },
        {
          title: "操作",
          key: "action",
          align: "center",
          width: 320,
          fixed: "right",
          render: (h, params) => {
            return h("div", [
              h(
                "Button",
                {
                  props: {
                    type: "primary",
                    size: "small"
                  },
                  style: {
                    marginRight: "5px"
                  },
                  on: {
                    click: () => {
                      this.detail(params.row);
                    }
                  }
                },
                "申请详情"
              ),
              h(
                "Button",
                {
                  props: {
                    size: "small",
                    type: "success"
                  },
                  style: {
                    marginRight: "5px"
                  },
                  on: {
                    click: () => {
                      this.pass(params.row);
                    }
                  }
                },
                "通过"
              ),
              h(
                "Button",
                {
                  props: {
                    size: "small",
                    type: "warning"
                  },
                  style: {
                    marginRight: "5px"
                  },
                  on: {
                    click: () => {
                      this.back(params.row);
                    }
                  }
                },
                "驳回"
              ),
              h(
                "Button",
                {
                  props: {
                    size: "small"
                  },
                  style: {
                    marginRight: "5px"
                  },
                  on: {
                    click: () => {
                      this.delegateTask(params.row);
                    }
                  }
                },
                "委托"
              ),
              h(
                "Button",
                {
                  props: {
                    size: "small",
                    type: "info"
                  },
                  style: {
                    marginRight: "5px"
                  },
                  on: {
                    click: () => {
                      this.history(params.row);
                    }
                  }
                },
                "审批历史"
              )
            ]);
          }
        }
      ],
      data: [], // 表单数据
      total: 0, // 表单数据总数
      deleteId: "",
      dictPriority: []
    };
  },
  methods: {
    init() {
      this.getDictDataType();
      this.getDataList();
    },
    getDictDataType() {
      getDictDataByType("priority").then(res => {
        if (res.success) {
          this.dictPriority = res.result;
        }
      });
    },
    searchUser(v) {
      if (!v) {
        return;
      }
      searchUserByName(v).then(res => {
        if (res.success) {
          this.userList = res.result;
        }
      });
    },
    changePage(v) {
      this.searchForm.pageNumber = v;
      this.getDataList();
      this.clearSelectAll();
    },
    changePageSize(v) {
      this.searchForm.pageSize = v;
      this.getDataList();
    },
    getDataList() {
      this.loading = true;
      todoList(this.searchForm).then(res => {
        this.loading = false;
        if (res.success === true) {
          this.data = res.result.content;
          this.total = res.result.totalElements;
        }
      });
    },
    handleSearch() {
      this.searchForm.pageNumber = 1;
      this.searchForm.pageSize = 10;
      this.getDataList();
    },
    handleReset() {
      this.$refs.searchForm.resetFields();
      this.searchForm.pageNumber = 1;
      this.searchForm.pageSize = 10;
      // 重新加载数据
      this.getDataList();
    },
    showSelect(e) {
      this.selectList = e;
      this.selectCount = e.length;
    },
    clearSelectAll() {
      this.$refs.table.selectAll(false);
    },
    handelSubmit() {
      this.submitLoading = true;
      if (this.form.type == 0) {
        // 通过
        if (this.showAssign && !this.form.assignee) {
          this.error = "请选择下一审批人";
          this.submitLoading = false;
          return;
        } else {
          this.error = "";
        }
        pass(this.form).then(res => {
          this.submitLoading = false;
          if (res.success) {
            this.$Message.success("操作成功");
            this.modalTaskVisible = false;
            this.getDataList();
          }
        });
      } else if (this.form.type == 1) {
        // 驳回
        back(this.form).then(res => {
          this.submitLoading = false;
          if (res.success) {
            this.$Message.success("操作成功");
            this.modalTaskVisible = false;
            this.getDataList();
          }
        });
      } else if (this.form.type == 2) {
        // 委托
        if (!this.form.userId) {
          this.error = "请搜索选择一委托人";
          this.submitLoading = false;
          return;
        } else {
          this.error = "";
        }
        delegate(this.form).then(res => {
          this.submitLoading = false;
          if (res.success) {
            this.$Message.success("操作成功");
            this.modalTaskVisible = false;
            this.getDataList();
          }
        });
      }
    },
    handelSubmitDel() {
      this.submitLoading = true;
      if (this.modalType === 0) {
        deleteTask(this.deleteId).then(res => {
          this.submitLoading = false;
          if (res.success) {
            this.$Message.success("操作成功");
            this.modalVisible = false;
            this.getDataList();
          }
        });
      } else if (this.modalType === 1) {
        let ids = "";
        this.selectList.forEach(function(e) {
          ids += e.id + ",";
        });
        ids = ids.substring(0, ids.length - 1);
        // 批量删除
        deleteTask(ids).then(res => {
          this.submitLoading = false;
          if (res.success) {
            this.$Message.success("操作成功");
            this.modalVisible = false;
            this.clearSelectAll();
            this.getDataList();
          }
        });
      }
    },
    detail(v) {
      let query = {
        id: v.tableId,
        type: 3,
        backRoute: this.$route.name
      };
      this.$router.push({
        name: v.routeName,
        query: query
      });
    },
    pass(v) {
      this.modalTaskTitle = "审批通过";
      this.form.id = v.id;
      this.form.procInstId = v.procInstId;
      this.form.priority = v.priority;
      this.form.type = 0;
      this.userLoading = true;
      getNextNode(v.procInstId).then(res => {
        this.userLoading = false;
        if (res.success) {
          if (res.result.users && res.result.users.length > 0) {
            this.showAssign = true;
          } else {
            this.showAssign = false;
          }
          this.assigneeList = res.result.users;
          this.modalTaskVisible = true;
        }
      });
    },
    back(v) {
      this.modalTaskTitle = "审批驳回";
      this.form.id = v.id;
      this.form.procInstId = v.procInstId;
      this.form.type = 1;
      this.showAssign = false;
      this.modalTaskVisible = true;
    },
    delegateTask(v) {
      this.modalTaskTitle = "委托他人代办";
      this.form.id = v.id;
      this.form.procInstId = v.procInstId;
      this.form.type = 2;
      this.showAssign = false;
      this.modalTaskVisible = true;
    },
    history(v) {
      let query = { id: v.procInstId, backRoute: this.$route.name };
      this.$router.push({
        name: "historic_detail",
        query: query
      });
    },
    remove(v) {
      this.modalTitle = `确认删除 ${v.name}`;
      // 单个删除
      this.deleteId = v.id;
      this.modalType = 0;
      this.modalVisible = true;
    },
    delAll() {
      if (this.selectCount <= 0) {
        this.$Message.warning("您还未选择要删除的数据");
        return;
      }
      this.modalTitle = "确认要删除所选的 " + this.selectCount + " 条数据";
      // 批量删除
      this.modalType = 1;
      this.modalVisible = true;
    }
  },
  mounted() {
    this.init();
  },
  watch: {
    // 监听路由变化
    $route(to, from) {
      if (to.name == "todo-manage") {
        this.getDataList();
      }
    }
  }
};
</script>