(window["webpackJsonp"]=window["webpackJsonp"]||[]).push([["chunk-32df7b10"],{"386d":function(e,t,a){"use strict";var n=a("cb7c"),l=a("83a1"),r=a("5f1b");a("214f")("search",1,(function(e,t,a,s){return[function(a){var n=e(this),l=void 0==a?void 0:a[t];return void 0!==l?l.call(a,n):new RegExp(a)[t](String(n))},function(e){var t=s(a,e,this);if(t.done)return t.value;var i=n(e),o=String(this),u=i.lastIndex;l(u,0)||(i.lastIndex=0);var c=r(i,o);return l(i.lastIndex,u)||(i.lastIndex=u),null===c?-1:c.index}]}))},"83a1":function(e,t){e.exports=Object.is||function(e,t){return e===t?0!==e||1/e===1/t:e!=e&&t!=t}},b564:function(e,t,a){"use strict";a("d74d")},d522:function(e,t,a){"use strict";var n=function(){var e=this,t=e.$createElement,a=e._self._c||t;return a("div",{staticClass:"panel"},[a("el-row",[a("el-col",{attrs:{lg:24,md:24,xs:24}},[a("div",{staticClass:"table-head table-search"},[a("el-col",{attrs:{lg:24,md:24,xs:24}},[a("el-row",{attrs:{gutter:6,type:"flex",align:"middle",justify:"end"}},[a("el-col",{attrs:{lg:5,md:8,xs:12}},[a("el-select",{attrs:{size:"small",placeholder:"消耗类型"},on:{change:e.search},model:{value:e.param.action,callback:function(t){e.$set(e.param,"action",t)},expression:"param.action"}},e._l(e.typeList,(function(e){return a("el-option",{key:e.value,attrs:{label:e.label,value:e.value}})})),1)],1),e._v(" "),a("el-date-picker",{staticClass:"date-input",attrs:{size:"small",type:"daterange","range-separator":"至","start-placeholder":"创建日期","end-placeholder":"创建日期","value-format":"yyyy-MM-dd",editable:!1},on:{change:e.timeChange},model:{value:e.startAndEnd,callback:function(t){e.startAndEnd=t},expression:"startAndEnd"}}),e._v(" "),a("el-button",{attrs:{type:"primary",size:"small"},on:{click:e.search}},[a("i",{staticClass:"el-icon-search"}),e._v(" 查询\n            ")])],1)],1)],1)])],1),e._v(" "),a("el-row",[a("el-table",{directives:[{name:"loading",rawName:"v-loading",value:e.isLoading,expression:"isLoading"}],attrs:{data:e.tableData,"element-loading-text":"Loading",border:"",fit:"","highlight-current-row":""}},[a("el-table-column",{attrs:{label:"序号",align:"center",width:"95"},scopedSlots:e._u([{key:"default",fn:function(t){return[e._v("\n          "+e._s(t.$index)+"\n        ")]}}])}),e._v(" "),a("el-table-column",{attrs:{"class-name":"status-col",label:"数量",align:"center"},scopedSlots:e._u([{key:"default",fn:function(t){return[e._v("\n          "+e._s(t.row.influenceAmount)+"\n        ")]}}])}),e._v(" "),a("el-table-column",{attrs:{"class-name":"status-col",label:"剩余数量",align:"center"},scopedSlots:e._u([{key:"default",fn:function(t){return[e._v("\n          "+e._s(t.row.amount)+"\n        ")]}}])}),e._v(" "),a("el-table-column",{attrs:{"class-name":"status-col",label:"时间",align:"center"},scopedSlots:e._u([{key:"default",fn:function(t){return[e._v("\n          "+e._s(t.row.gmtCreate)+"\n        ")]}}])}),e._v(" "),a("el-table-column",{attrs:{"class-name":"status-col",label:"类型",align:"center"},scopedSlots:e._u([{key:"default",fn:function(t){return[e._v("\n          "+e._s(e._f("actionTypeText")(t.row.action))+"\n        ")]}}])}),e._v(" "),a("el-table-column",{attrs:{align:"center",prop:"created_at",label:"备注"},scopedSlots:e._u([{key:"default",fn:function(t){return[e._v("\n          "+e._s(t.row.remark)+"\n        ")]}}])})],1)],1),e._v(" "),a("el-pagination",{staticClass:"ygw-fenye",attrs:{background:"","current-page":e.param.page,"page-size":e.param.size,layout:"total,sizes,prev, pager, next, jumper",total:e.total},on:{"size-change":e.handleSizeChange,"current-change":e.handleCurrentChange,"update:currentPage":function(t){return e.$set(e.param,"page",t)},"update:current-page":function(t){return e.$set(e.param,"page",t)}}})],1)},l=[],r=(a("c5f6"),a("386d"),a("db72")),s=a("2f62"),i={data:function(){return{isLoading:!1,total:0,tableData:[],startAndEnd:"",param:{userId:"",itemId:"",endTime:"",startTime:"",action:"",page:1,size:20},typeList:[{label:"全部",value:""},{label:"注册赠送",value:"INCREASE_REGISTER"},{label:"充值增加",value:"INCREASE_PAYMENT"},{label:"管理增加",value:"INCREASE_MANAGER"},{label:"用户注册赠送扣除",value:"DECREASE_REGISTER"},{label:"管理扣除",value:"DECREASE_MANAGER"},{label:"用户充值扣除",value:"DECREASE_PAYMENT"},{label:"使用扣除",value:"DECREASE_CONSUME"}]}},methods:Object(r["a"])(Object(r["a"])({},Object(s["b"])("admin",{getData:"userQueryStatementPage"})),{},{timeChange:function(e){this.param.startTime=e[0],this.param.endTime=e[1],this.search()},getList:function(){var e=this;this.isLoading=!0,this.getData(this.param).then((function(t){e.tableData=t.data?t.data.dataList:[],e.total=Number(t.data.total),e.isLoading=!1}))},handleCurrentChange:function(e){this.param.page=e,this.getList()},handleSizeChange:function(e){this.param.size=e,this.getList()},initParam:function(){this.param.page=1,this.param.startTime="",this.param.endTime=""},search:function(){this.param.page=1,this.getList()}})},o=i,u=a("2877"),c=Object(u["a"])(o,n,l,!1,null,null,null);t["a"]=c.exports},d74d:function(e,t,a){},f608:function(e,t,a){"use strict";a.r(t);var n=function(){var e=this,t=e.$createElement,a=e._self._c||t;return a("div",{staticClass:"panel"},[a("el-row",[a("el-col",{attrs:{lg:24,md:24,xs:24}},[a("div",{staticClass:"table-head table-search"},[a("el-col",{attrs:{lg:24,md:24,xs:24}},[a("el-row",{attrs:{gutter:6,type:"flex",align:"middle",justify:"end"}},[a("span",[e._v("关键字")]),e._v(" "),a("el-col",{attrs:{lg:3,md:8,xs:12}},[a("el-input",{staticClass:"search-input",attrs:{size:"small",placeholder:"请输入注册账号/ID/UID"},model:{value:e.param.username,callback:function(t){e.$set(e.param,"username",t)},expression:"param.username"}})],1),e._v(" "),a("el-button",{attrs:{type:"primary",size:"small"},on:{click:e.search}},[a("i",{staticClass:"el-icon-search"}),e._v(" 查询\n            ")])],1)],1)],1)])],1),e._v(" "),a("el-row",[a("el-table",{directives:[{name:"loading",rawName:"v-loading",value:e.isLoading,expression:"isLoading"}],attrs:{data:e.tableData,"element-loading-text":"Loading",border:"",fit:"","highlight-current-row":"",height:e.height}},[a("el-table-column",{attrs:{label:"用户ID",align:"center",width:"180"},scopedSlots:e._u([{key:"default",fn:function(t){return[a("el-button",{attrs:{type:"text"},on:{click:function(a){return e.seelevel(t.row.id)}}},[e._v(e._s(t.row.id))])]}}])}),e._v(" "),a("el-table-column",{attrs:{label:"UID",align:"center",width:"100"},scopedSlots:e._u([{key:"default",fn:function(t){return[a("span",[e._v(e._s(t.row.uid))])]}}])}),e._v(" "),a("el-table-column",{attrs:{"class-name":"status-col",label:"注册账号",align:"center"},scopedSlots:e._u([{key:"default",fn:function(t){return[e._v("\n          "+e._s(t.row.username)+"\n        ")]}}])}),e._v(" "),a("el-table-column",{attrs:{"class-name":"status-col",label:"注册IP",align:"center"},scopedSlots:e._u([{key:"default",fn:function(t){return[e._v("\n          "+e._s(t.row.registerIp)+"\n        ")]}}])}),e._v(" "),a("el-table-column",{attrs:{"class-name":"status-col",label:"注册时间",align:"center"},scopedSlots:e._u([{key:"default",fn:function(t){return[e._v("\n          "+e._s(t.row.gmtCreate||"--")+"\n        ")]}}])}),e._v(" "),a("el-table-column",{attrs:{label:"最后登陆时间",align:"center"},scopedSlots:e._u([{key:"default",fn:function(t){return[e._v("\n          "+e._s(t.row.lastLoginTime||"--")+"\n        ")]}}])}),e._v(" "),a("el-table-column",{attrs:{align:"center",prop:"lastLoginIp",label:"登录IP",width:"180"},scopedSlots:e._u([{key:"default",fn:function(t){return[a("span",[e._v(e._s(t.row.lastLoginIp||"--"))])]}}])}),e._v(" "),a("el-table-column",{attrs:{align:"center",prop:"mobilePhone",label:"手机号",width:"180"},scopedSlots:e._u([{key:"default",fn:function(t){return[a("span",[e._v(e._s(t.row.mobilePhone||"--"))])]}}])}),e._v(" "),a("el-table-column",{attrs:{align:"center",prop:"balance",label:"用户余额",width:"180",sortable:""},scopedSlots:e._u([{key:"default",fn:function(t){return[a("span",[e._v(e._s(t.row.balance||"--"))])]}}])}),e._v(" "),a("el-table-column",{attrs:{align:"center",prop:"created_at",label:"操作",fixed:"right",width:"280"},scopedSlots:e._u([{key:"default",fn:function(t){return[a("el-button",{attrs:{type:"success",icon:"el-icon-plus",size:"mini"},on:{click:function(a){return e.upDown(t.row,"CREDIT")}}},[e._v("上分")]),e._v(" "),a("el-button",{attrs:{type:"warning",icon:"el-icon-minus",size:"mini"},on:{click:function(a){return e.upDown(t.row,"DEBIT")}}},[e._v("下分")]),e._v(" "),a("el-button",{attrs:{title:"查看信息",size:"mini"},on:{click:function(a){return e.openPayment(t.row)}}},[e._v("查看信息")])]}}])})],1)],1),e._v(" "),a("el-pagination",{staticClass:"ygw-fenye",attrs:{background:"","current-page":e.param.page,"page-size":e.param.size,layout:"total,sizes,prev, pager, next, jumper",total:e.total},on:{"size-change":e.handleSizeChange,"current-change":e.handleCurrentChange,"update:currentPage":function(t){return e.$set(e.param,"page",t)},"update:current-page":function(t){return e.$set(e.param,"page",t)}}}),e._v(" "),a("el-dialog",{attrs:{visible:e.showAdd,title:"基本信息",width:"600px"},on:{"update:visible":function(t){e.showAdd=t},closed:e.dialogClose}},[e.detail?a("el-row",{attrs:{type:"flex"}},[a("el-col",[a("div",{staticClass:"user-info"},[a("p",[a("span",[e._v("账户余额：")]),e._v(e._s(e.detail.balance||"--"))]),e._v(" "),a("p",[a("span",[e._v("邮箱：")]),e._v(e._s(e.detail.email||"--"))]),e._v(" "),a("p",[a("span",[e._v("名字：")]),e._v(e._s(e.detail.name||"--"))]),e._v(" "),a("p",[a("span",[e._v("姓氏：")]),e._v(e._s(e.detail.surname||"--"))]),e._v(" "),a("p",[a("span",[e._v("街道地址：")]),e._v(e._s(e.detail.address||"--"))]),e._v(" "),a("p",[a("span",[e._v("街道地址（续）：")]),e._v(e._s(e.detail.addressDetails||"--")+"\n          ")]),e._v(" "),a("p",[a("span",[e._v("邮政编码：")]),e._v(e._s(e.detail.postalCode||"--"))]),e._v(" "),a("p",[a("span",[e._v("城市：")]),e._v(e._s(e.detail.city||"--"))]),e._v(" "),a("p",[a("span",[e._v("国家：")]),e._v(e._s(e.detail.country||"--"))]),e._v(" "),a("p",[a("span",[e._v("手机号码 ：")]),e._v(e._s(e.detail.mobilePhone||"--"))])])])],1):e._e()],1),e._v(" "),a("el-dialog",{attrs:{visible:e.showUpDown,title:"上下分设置",width:"400px"},on:{"update:visible":function(t){e.showUpDown=t},closed:e.dialogClose2}},[a("el-form",{ref:"forms",attrs:{model:e.form,rules:e.formRules,"label-width":"80px"}},[a("el-form-item",{attrs:{label:"CREDIT"==e.form.creditDebit?"上分金额":"下分金额",prop:"amount"}},[a("el-input",{model:{value:e.form.amount,callback:function(t){e.$set(e.form,"amount",t)},expression:"form.amount"}})],1),e._v(" "),a("el-form-item",{attrs:{label:"备注"}},[a("el-input",{model:{value:e.form.applyRemark,callback:function(t){e.$set(e.form,"applyRemark",t)},expression:"form.applyRemark"}})],1),e._v(" "),a("el-form-item",[a("el-button",{attrs:{type:"primary"},on:{click:e.onSubmit}},[e._v("确定")]),e._v(" "),a("el-button",{on:{click:e.dialogClose2}},[e._v("取消")])],1)],1)],1)],1)},l=[],r=(a("7514"),a("ac6a"),a("c5f6"),a("386d"),a("db72")),s=(a("96cf"),a("3b8d")),i=a("2f62"),o=a("ffc7"),u=a("d522"),c={components:{recharge:o["a"],consume:u["a"]},data:function(){return{brokerData:[],showAdd:!1,showUpDown:!1,isLoading:!1,showRecharge:!1,showConsume:!1,loading:!1,total:0,tableData:[],startAndEnd:"",param:{username:null,page:1,size:20},openType:!0,detail:null,form:{amount:"",applyRemark:"",creditDebit:"CREDIT",currency:"INR",userId:0},formRules:{amount:[{required:!0,message:"请输入上分金额",trigger:"blur"}]},brokerTotalCount:0,height:"500px"}},created:function(){var e=Object(s["a"])(regeneratorRuntime.mark((function e(){var t;return regeneratorRuntime.wrap((function(e){while(1)switch(e.prev=e.next){case 0:return t=document.documentElement.clientHeight||document.body.clientHeight,this.height=t-270+"px",e.next=4,this.getList();case 4:case"end":return e.stop()}}),e,this)})));function t(){return e.apply(this,arguments)}return t}(),watch:{},methods:Object(r["a"])(Object(r["a"])({},Object(i["b"])("user",{queryUserPage:"queryUserPage",queryAccountList:"queryAccountList",movementapply:"movementapply"})),{},{seelevel:function(e){this.$router.push({path:"/seelevel/list",query:{userId:e}})},dialogClose:function(){this.showAdd=!1},dialogClose2:function(){this.showUpDown=!1,this.form.amount="",this.form.applyRemark=""},openPayment:function(e){this.detail=e,this.showAdd=!0},handleClick:function(){this.search()},handleCurrentChange:function(e){this.param.page=e,this.getList()},handleSizeChange:function(e){this.param.size=e,this.getList()},search:function(){this.param.page=1,this.getList()},upDown:function(e,t){this.form.userId=e.id,this.form.creditDebit=t,this.showUpDown=!0},getList:function(){var e=this;this.isLoading=!0,this.queryUserPage(this.param).then((function(t){if(0==t.code){var a=t.data.records||[],n=a.map((function(e){return e.id}));e.getqueryAccountList(a,n),e.total=Number(t.data.total)}e.isLoading=!1})).catch((function(t){e.tableData=[],e.isLoading=!1}))},getqueryAccountList:function(e,t){var a=this;this.queryAccountList({uidList:t,currency:"INR"}).then((function(t){if(0==t.code){var n=t.data||[];e.forEach((function(e,t){var a=n.find((function(t){return t.userId==e.id}));e.balance=a?a.balance:0}))}a.tableData=e}))},onSubmit:function(){var e=this;this.$refs["forms"].validate(function(){var t=Object(s["a"])(regeneratorRuntime.mark((function t(a){return regeneratorRuntime.wrap((function(t){while(1)switch(t.prev=t.next){case 0:if(!a){t.next=4;break}e.movementapply(e.form).then((function(t){e.dialogClose2(),e.search()})),t.next=6;break;case 4:return console.log("error submit!!"),t.abrupt("return",!1);case 6:case"end":return t.stop()}}),t)})));return function(e){return t.apply(this,arguments)}}())}})},d=c,p=(a("b564"),a("2877")),m=Object(p["a"])(d,n,l,!1,null,"5860061a",null);t["default"]=m.exports},ffc7:function(e,t,a){"use strict";var n=function(){var e=this,t=e.$createElement,a=e._self._c||t;return a("div",{staticClass:"panel"},[a("el-row",[a("el-col",{attrs:{lg:24,md:24,xs:24}},[a("div",{staticClass:"table-head table-search"},[a("el-col",{attrs:{lg:24,md:24,xs:24}},[a("el-row",{attrs:{gutter:6,type:"flex",align:"middle",justify:"end"}},[a("el-col",{attrs:{lg:5,md:8,xs:12}},[a("el-select",{attrs:{size:"small",placeholder:"充值方式"},on:{change:e.search},model:{value:e.param.type,callback:function(t){e.$set(e.param,"type",t)},expression:"param.type"}},e._l(e.typeList,(function(e){return a("el-option",{key:e.value,attrs:{label:e.label,value:e.value}})})),1)],1),e._v(" "),a("el-date-picker",{staticClass:"date-input",attrs:{size:"small",type:"daterange","range-separator":"至","start-placeholder":"充值日期","end-placeholder":"充值日期","value-format":"yyyy-MM-dd",editable:!1},on:{change:e.timeChange},model:{value:e.startAndEnd,callback:function(t){e.startAndEnd=t},expression:"startAndEnd"}}),e._v(" "),a("el-button",{attrs:{type:"primary",size:"small"},on:{click:e.search}},[a("i",{staticClass:"el-icon-search"}),e._v(" 查询\n            ")])],1)],1)],1)])],1),e._v(" "),a("el-row",[a("el-table",{directives:[{name:"loading",rawName:"v-loading",value:e.isLoading,expression:"isLoading"}],attrs:{data:e.tableData,"element-loading-text":"Loading",border:"",fit:"","highlight-current-row":""}},[a("el-table-column",{attrs:{label:"序号",align:"center",width:"95"},scopedSlots:e._u([{key:"default",fn:function(t){return[e._v("\n          "+e._s(t.$index)+"\n        ")]}}])}),e._v(" "),a("el-table-column",{attrs:{"class-name":"status-col",label:"充值金额",align:"center"},scopedSlots:e._u([{key:"default",fn:function(t){return[e._v("\n          "+e._s(t.row.payAmount)+"\n        ")]}}])}),e._v(" "),a("el-table-column",{attrs:{"class-name":"status-col",label:"充值条数",align:"center"},scopedSlots:e._u([{key:"default",fn:function(t){return[e._v("\n          "+e._s(t.row.amount)+"\n        ")]}}])}),e._v(" "),a("el-table-column",{attrs:{"class-name":"status-col",label:"充值方式",align:"center"},scopedSlots:e._u([{key:"default",fn:function(t){return[e._v("\n          "+e._s(e._f("getTypeName")(t.row.type))+"\n        ")]}}])}),e._v(" "),a("el-table-column",{attrs:{"class-name":"status-col",label:"充值时间",align:"center"},scopedSlots:e._u([{key:"default",fn:function(t){return[e._v("\n          "+e._s(t.row.gmtCreate)+"\n        ")]}}])}),e._v(" "),a("el-table-column",{attrs:{align:"center",prop:"created_at",label:"备注"},scopedSlots:e._u([{key:"default",fn:function(t){return[e._v("\n          "+e._s(t.row.remark)+"\n        ")]}}])})],1)],1),e._v(" "),a("el-pagination",{staticClass:"ygw-fenye",attrs:{background:"","current-page":e.param.page,"page-size":e.param.size,layout:"total,sizes,prev, pager, next, jumper",total:e.total},on:{"size-change":e.handleSizeChange,"current-change":e.handleCurrentChange,"update:currentPage":function(t){return e.$set(e.param,"page",t)},"update:current-page":function(t){return e.$set(e.param,"page",t)}}})],1)},l=[],r=(a("c5f6"),a("386d"),a("db72")),s=a("2f62"),i={data:function(){return{isLoading:!1,total:0,tableData:[],startAndEnd:"",param:{userId:"",itemId:"",startTime:"",endTime:"",type:"",page:1,size:10},typeList:[{label:"全部",value:""},{label:"支付宝扫码自动充值",value:1},{label:"支付宝转账人工充值",value:2},{label:"微信转账人工充值",value:3},{label:"银行卡转账人工充值",value:4}]}},methods:Object(r["a"])(Object(r["a"])({},Object(s["b"])("admin",{getData:"userQueryPaymentPage"})),{},{timeChange:function(e){this.param.startTime=e[0],this.param.endTime=e[1],this.search()},getList:function(){var e=this;this.isLoading=!0,this.getData(this.param).then((function(t){e.tableData=t.data?t.data.dataList:[],e.total=Number(t.data.total),e.isLoading=!1}))},handleCurrentChange:function(e){this.param.page=e,this.getList()},handleSizeChange:function(e){this.param.size=e,this.getList()},initParam:function(){this.param.page=1,this.param.name="",this.param.phone=""},search:function(){this.param.page=1,this.getList()}})},o=i,u=a("2877"),c=Object(u["a"])(o,n,l,!1,null,null,null);t["a"]=c.exports}}]);