(window["webpackJsonp"]=window["webpackJsonp"]||[]).push([["chunk-1359b9e4"],{"0099":function(t,e,a){"use strict";a("e421")},"386d":function(t,e,a){"use strict";var n=a("cb7c"),s=a("83a1"),i=a("5f1b");a("214f")("search",1,(function(t,e,a,o){return[function(a){var n=t(this),s=void 0==a?void 0:a[e];return void 0!==s?s.call(a,n):new RegExp(a)[e](String(n))},function(t){var e=o(a,t,this);if(e.done)return e.value;var l=n(t),r=String(this),u=l.lastIndex;s(u,0)||(l.lastIndex=0);var c=i(l,r);return s(l.lastIndex,u)||(l.lastIndex=u),null===c?-1:c.index}]}))},"83a1":function(t,e){t.exports=Object.is||function(t,e){return t===e?0!==t||1/t===1/e:t!=t&&e!=e}},e421:function(t,e,a){},f199:function(t,e,a){"use strict";a.r(e);var n=function(){var t=this,e=t.$createElement,a=t._self._c||e;return a("div",{staticClass:"panel"},[a("el-row",[a("el-col",{attrs:{lg:24,md:24,xs:24}},[a("div",{staticClass:"table-head table-search"},[a("el-col",{attrs:{lg:8,md:8,xs:24}},[a("el-tag",{attrs:{type:"success"}},[a("p",[a("span",[t._v("当前总提现：")]),t._v(" "),a("strong",[t._v(t._s(t._f("scientificToNumber")(t.totalAmount1))+" INR/\n                "+t._s(t._f("scientificToNumber")(.084*t.totalAmount1))+"元\n              ")])])]),t._v(" "),a("el-tag",{attrs:{type:"error"}},[a("p",[a("span",[t._v("当前总到账：")]),t._v(" "),a("strong",[t._v(t._s(t._f("scientificToNumber")(t.totalAmount2))+" INR/\n                "+t._s(t._f("scientificToNumber")(.084*t.totalAmount2))+"元")])])])],1),t._v(" "),a("el-col",{attrs:{lg:16,md:16,xs:24}},[a("el-row",{attrs:{gutter:6,type:"flex",align:"middle",justify:"end"}},[a("span",[t._v("用户ID")]),t._v(" "),a("el-col",{attrs:{lg:3,md:8,xs:12}},[a("el-input",{staticClass:"search-input",attrs:{clearable:"",size:"small",placeholder:"请输入用户ID"},on:{clear:function(e){t.param.uidList=""}},model:{value:t.param.uidList,callback:function(e){t.$set(t.param,"uidList",e)},expression:"param.uidList"}})],1),t._v(" "),a("span",[t._v("手机号")]),t._v(" "),a("el-col",{attrs:{lg:3,md:8,xs:12}},[a("el-input",{staticClass:"search-input",attrs:{clearable:"",size:"small",placeholder:"请输入用户手机号"},on:{clear:function(e){t.param.mobilePhone=""}},model:{value:t.param.mobilePhone,callback:function(e){t.$set(t.param,"mobilePhone",e)},expression:"param.mobilePhone"}})],1),t._v(" "),a("span",[t._v("提现时间")]),t._v(" "),a("el-date-picker",{staticClass:"date-input",attrs:{size:"small",type:"daterange","range-separator":"至","start-placeholder":"开始时间","end-placeholder":"结束时间",format:"yyyy-MM-dd","value-format":"yyyy-MM-dd HH:mm:ss","picker-options":t.pickerOptions,"default-time":["00:00:00","23:59:59"]},on:{change:t.changeTime},model:{value:t.dateTime,callback:function(e){t.dateTime=e},expression:"dateTime"}}),t._v(" "),a("span",[t._v("提现状态")]),t._v(" "),a("el-select",{attrs:{size:"small",placeholder:"充值/提现"},on:{change:t.search},model:{value:t.param.status,callback:function(e){t.$set(t.param,"status",e)},expression:"param.status"}},t._l(t.statusList,(function(t){return a("el-option",{key:t.id,attrs:{label:t.name,value:t.type}})})),1),t._v(" "),a("el-button",{attrs:{type:"primary",size:"small"},on:{click:t.search}},[a("i",{staticClass:"el-icon-search"}),t._v(" 查询\n            ")])],1)],1)],1)])],1),t._v(" "),a("el-row",[a("el-table",{directives:[{name:"loading",rawName:"v-loading",value:t.isLoading,expression:"isLoading"}],attrs:{data:t.tableData,"element-loading-text":"Loading",border:"",fit:"","highlight-current-row":"",height:t.height}},[a("el-table-column",{attrs:{label:"用户ID",align:"center",width:"180"},scopedSlots:t._u([{key:"default",fn:function(e){return[a("span",[t._v(t._s(e.row.userId))])]}}])}),t._v(" "),a("el-table-column",{attrs:{label:"用户账号",align:"center",width:"180"},scopedSlots:t._u([{key:"default",fn:function(e){return[a("span",[t._v(t._s(e.row.username))])]}}])}),t._v(" "),a("el-table-column",{attrs:{label:"手机号",align:"center",width:"180"},scopedSlots:t._u([{key:"default",fn:function(e){return[a("span",[t._v(t._s(e.row.mobilePhone))])]}}])}),t._v(" "),a("el-table-column",{attrs:{label:"是否刷子",align:"center",width:"180"},scopedSlots:t._u([{key:"default",fn:function(e){return[a("span",[t._v(t._s(e.row.isBlack?"是":"否"))])]}}])}),t._v(" "),a("el-table-column",{attrs:{label:"提现订单",align:"center",width:"180"},scopedSlots:t._u([{key:"default",fn:function(e){return[a("span",[t._v(t._s(e.row.orderNo))])]}}])}),t._v(" "),a("el-table-column",{attrs:{label:"提现方式",align:"center"},scopedSlots:t._u([{key:"default",fn:function(e){return[a("span",[t._v(t._s(e.row.payMethodName))])]}}])}),t._v(" "),a("el-table-column",{attrs:{label:"提现来源",align:"center"},scopedSlots:t._u([{key:"default",fn:function(e){return[a("span",[t._v(t._s(e.row.payApiName))])]}}])}),t._v(" "),a("el-table-column",{attrs:{label:"提现金额",align:"center",prop:"reqNum",width:"120",sortable:""},scopedSlots:t._u([{key:"default",fn:function(e){return[a("span",[t._v(t._s(e.row.reqNum||0))])]}}])}),t._v(" "),a("el-table-column",{attrs:{label:"手续费",align:"center"},scopedSlots:t._u([{key:"default",fn:function(e){return[a("span",[t._v(t._s(e.row.fee||0))])]}}])}),t._v(" "),a("el-table-column",{attrs:{label:"到账金额",align:"center"},scopedSlots:t._u([{key:"default",fn:function(e){return[a("span",[t._v(t._s(e.row.realNum||0))])]}}])}),t._v(" "),a("el-table-column",{attrs:{label:"累计提现金额",align:"center",width:"120"},scopedSlots:t._u([{key:"default",fn:function(e){return[a("span",[t._v(t._s(e.row.totalWithdrawAmount||0))])]}}])}),t._v(" "),a("el-table-column",{attrs:{label:"累计入金金额",align:"center",width:"120"},scopedSlots:t._u([{key:"default",fn:function(e){return[a("span",[t._v(t._s(e.row.totalRechargeAmount||0))])]}}])}),t._v(" "),a("el-table-column",{attrs:{label:"邀请奖励",align:"center"},scopedSlots:t._u([{key:"default",fn:function(e){return[a("span",[t._v(t._s(e.row.totalInviteAmount||0))])]}}])}),t._v(" "),a("el-table-column",{attrs:{label:"登录奖励",align:"center"},scopedSlots:t._u([{key:"default",fn:function(e){return[a("span",[t._v(t._s(e.row.totalLoginAmount||0))])]}}])}),t._v(" "),a("el-table-column",{attrs:{label:"提成收益",align:"center"},scopedSlots:t._u([{key:"default",fn:function(e){return[a("span",[t._v(t._s(e.row.totalDivideAmount||0))])]}}])}),t._v(" "),a("el-table-column",{attrs:{label:"邀请人数",align:"center",prop:"invitePeople",width:"120",sortable:""},scopedSlots:t._u([{key:"default",fn:function(e){return[a("span",[t._v(t._s(e.row.invitePeople||0))])]}}])}),t._v(" "),a("el-table-column",{attrs:{label:"邀请入金人数",align:"center",width:"120"},scopedSlots:t._u([{key:"default",fn:function(e){return[a("span",[t._v(t._s(e.row.inviteRechargePeople||0))])]}}])}),t._v(" "),a("el-table-column",{attrs:{label:"提现状态",align:"center"},scopedSlots:t._u([{key:"default",fn:function(e){return[a("span",[t._v(t._s(t._f("statusText")(e.row.status)))])]}}])}),t._v(" "),a("el-table-column",{attrs:{label:"备注",align:"center",width:"120"},scopedSlots:t._u([{key:"default",fn:function(e){return[a("span",[t._v(t._s(e.row.remark||"-"))])]}}])}),t._v(" "),a("el-table-column",{attrs:{label:"提现时间",align:"center",width:"155"},scopedSlots:t._u([{key:"default",fn:function(e){return[a("span",[t._v(t._s(e.row.gmtCreate||"-"))])]}}])}),t._v(" "),a("el-table-column",{attrs:{label:"到账时间",align:"center",width:"155"},scopedSlots:t._u([{key:"default",fn:function(e){return[a("span",[t._v(t._s(e.row.arrivalTime||"-"))])]}}])}),t._v(" "),a("el-table-column",{attrs:{fixed:"right",label:"操作",align:"center",width:"295"},scopedSlots:t._u([{key:"default",fn:function(e){return["SUCCESSFUL"!=e.row.status&&"PROCESSING"!=e.row.status&&"FAILED"!=e.row.status&&"NOTPASS"!=e.row.status?[a("el-button",{attrs:{type:"danger",size:"mini"},on:{click:function(a){return t.notPress(e.row)}}},[t._v("不通过")]),t._v(" "),a("el-button",{attrs:{type:"success",size:"mini",loading:e.row.isPost},on:{click:function(a){return t.sysProcess(e.row)}}},[t._v("系统出金")]),t._v(" "),a("el-button",{attrs:{type:"success",size:"mini",loading:e.row.isPost2},on:{click:function(a){return t.onProcess(e.row)}}},[t._v("线下出金")])]:"FAILED"==e.row.status?a("el-button",{attrs:{size:"mini",disabled:""}},[t._v("提现失败")]):"PROCESSING"==e.row.status?a("el-button",{attrs:{size:"mini",disabled:""}},[t._v("等待处理")]):"NOTPASS"==e.row.status?a("el-button",{attrs:{size:"mini",disabled:""}},[t._v("不通过")]):a("el-button",{attrs:{size:"mini",disabled:""}},[t._v("提现成功")])]}}])})],1)],1),t._v(" "),a("el-pagination",{staticClass:"ygw-fenye",attrs:{background:"","current-page":t.param.page,"page-size":t.param.size,"page-sizes":[20,100,500,1e3],layout:"total,sizes,prev, pager, next, jumper",total:t.total},on:{"size-change":t.handleSizeChange,"current-change":t.handleCurrentChange,"update:currentPage":function(e){return t.$set(t.param,"page",e)},"update:current-page":function(e){return t.$set(t.param,"page",e)}}}),t._v(" "),a("el-dialog",{attrs:{visible:t.showEdit},on:{"update:visible":function(e){t.showEdit=e},closed:t.dialogClose}},[a("el-form",{ref:"form",attrs:{model:t.form,"label-width":"80px"}},[a("el-form-item",{attrs:{label:"备注"}},[a("el-input",{attrs:{type:"textarea",autosize:{mINRows:2,maxRows:4},placeholder:"请输入内容"},model:{value:t.form.remark,callback:function(e){t.$set(t.form,"remark",e)},expression:"form.remark"}})],1),t._v(" "),a("el-form-item",[a("el-button",{attrs:{type:"primary"},on:{click:t.onSubmit}},[t._v("确定")]),t._v(" "),a("el-button",{on:{click:t.dialogClose}},[t._v("取消")])],1)],1)],1)],1)},s=[],i=(a("7514"),a("c5f6"),a("ac6a"),a("db72")),o=(a("386d"),a("2f62")),l={data:function(){return{loading:!1,showEdit:!1,isLoading:!1,total:0,brokerData:[],tableData:[],param:{uidList:null,startTime:null,endTime:null,cashType:"WITHDRAW_OTC",status:"PENDING",page:1,size:20},statusList:[{id:0,type:"",name:"全部"},{id:5,type:"PENDING_SHUAZI",name:"待审核-刷子"},{id:1,type:"PENDING",name:"待审核"},{id:1,type:"PROCESSING",name:"处理中"},{id:2,type:"SUCCESSFUL",name:"提现成功"},{id:3,type:"FAILED",name:"提现失败"},{id:4,type:"NOTPASS",name:"不通过"}],form:{id:"",remark:""},isPost:!1,isPost2:!1,height:"500px",dateTime:"",pickerOptions:{shortcuts:[{text:"今天",onClick:function(t){var e=new Date,a=new Date((new Date).setHours(0,0,0,0));t.$emit("pick",[a,e])}},{text:"昨天",onClick:function(t){var e=new Date((new Date).setHours(23,59,59,0)),a=new Date((new Date).setHours(0,0,0,0));a.setTime(a.getTime()-864e5),e.setTime(e.getTime()-864e5),t.$emit("pick",[a,e])}},{text:"一周前",onClick:function(t){var e=new Date,a=new Date((new Date).setHours(0,0,0,0));a.setTime(a.getTime()-6048e5),t.$emit("pick",[a,e])}},{text:"最近一个月",onClick:function(t){var e=new Date,a=new Date((new Date).setHours(0,0,0,0));a.setTime(a.getTime()-2592e6),t.$emit("pick",[a,e])}},{text:"最近二个月",onClick:function(t){var e=new Date,a=new Date((new Date).setHours(0,0,0,0));a.setTime(a.getTime()-5184e6),t.$emit("pick",[a,e])}},{text:"最近三个月",onClick:function(t){var e=new Date,a=new Date((new Date).setHours(0,0,0,0));a.setTime(a.getTime()-7776e6),t.$emit("pick",[a,e])}}]},totalAmount1:0,totalAmount2:0}},watch:{dateTime:function(){this.dateTime||(this.param.startTime="",this.param.endTime="",this.search())}},filters:{statusText:function(t){switch(t){case"PENDING":return"待审核";case"PROCESSING":return"处理中";case"SUCCESSFUL":return"提现成功";case"FAILED":return"提现失败";case"NOTPASS":return"不通过";default:return""}}},created:function(){var t=document.documentElement.clientHeight||document.body.clientHeight;this.height=t-270+"px"},mounted:function(){this.getList()},methods:Object(i["a"])(Object(i["a"])({},Object(o["b"])({queryPage:"system/queryPage",notpass:"system/notpass",systemProcess:"system/systemProcess",underlinesuccessful:"system/underlinesuccessful",queryUserPage:"user/queryUserPage"})),{},{changeTime:function(t){this.param.startTime=t[0],this.param.endTime=t[1],this.search()},getList:function(){var t=this;this.isLoading=!0,this.totalAmount1=0,this.totalAmount2=0,this.queryPage(this.param).then((function(e){if(console.log(e),e&&0==e.code){var a=e.data.records||[];a.length&&a.forEach((function(e){e.isPost=!1,e.isPost2=!1,t.totalAmount1=t.totalAmount1+e.reqNum,t.totalAmount2=t.totalAmount2+e.realNum})),t.total=Number(e.data.total);var n=a.map((function(t){return t.userId}));t.getqueryAccountList(a,n)}t.isLoading=!1})).catch((function(e){t.tableData=[],t.isLoading=!1}))},getqueryAccountList:function(t,e){var a=this;this.queryUserPage({idList:e,page:1,size:100}).then((function(e){if(0==e.code){var n=e.data.records||[];t.forEach((function(t,e){var a=n.find((function(e){return e.id==t.userId}));t.isBlack=!!a&&a.isBlack})),a.tableData=t,console.log(a.tableData)}}))},handleCurrentChange:function(t){this.param.page=t,this.getList()},handleSizeChange:function(t){this.param.size=t,this.getList()},search:function(){this.param.page=1,this.getList()},dialogClose:function(){this.showEdit=!1},notPress:function(t){this.showEdit=!0,this.form.id=t.id},sysProcess:function(t){var e=this;t.isPost||(t.isPost=!0,this.systemProcess({id:t.id,passagewayId:t.passagewayId,realNum:t.realNum}).then((function(a){t.isPost=!1,e.dialogClose(),e.search()})))},onProcess:function(t){var e=this;t.isPost2||(t.isPost2=!0,this.underlinesuccessful({id:t.id}).then((function(a){t.isPost2=!1,e.dialogClose(),e.search()})))},onSubmit:function(){var t=this;this.notpass(this.form).then((function(e){t.dialogClose(),t.search()}))}})},r=l,u=(a("0099"),a("2877")),c=Object(u["a"])(r,n,s,!1,null,"70159aeb",null);e["default"]=c.exports}}]);