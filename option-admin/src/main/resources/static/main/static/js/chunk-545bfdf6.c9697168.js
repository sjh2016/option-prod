(window["webpackJsonp"]=window["webpackJsonp"]||[]).push([["chunk-545bfdf6"],{"14bf":function(e,t,a){"use strict";a.r(t);var n=function(){var e=this,t=e.$createElement,a=e._self._c||t;return a("div",{staticClass:"panel"},[a("el-row",[a("el-col",{attrs:{lg:24,md:24,xs:24}},[a("div",{staticClass:"table-head table-search"},[a("el-col",{attrs:{lg:24,md:24,xs:24}},[a("el-row",{attrs:{gutter:6,type:"flex",align:"middle",justify:"end"}},[a("span",[e._v("通道类型")]),e._v(" "),a("el-select",{attrs:{size:"small",placeholder:"充值/提现"},on:{change:e.search},model:{value:e.param.cashType,callback:function(t){e.$set(e.param,"cashType",t)},expression:"param.cashType"}},e._l(e.cashList,(function(e){return a("el-option",{key:e.id,attrs:{label:e.name,value:e.type}})})),1),e._v(" "),a("el-button",{attrs:{type:"primary",size:"small"},on:{click:e.search}},[a("i",{staticClass:"el-icon-search"}),e._v(" 查询\n            ")])],1)],1)],1)])],1),e._v(" "),a("el-row",[a("el-table",{directives:[{name:"loading",rawName:"v-loading",value:e.isLoading,expression:"isLoading"}],attrs:{data:e.tableData,"element-loading-text":"Loading",border:"",fit:"","highlight-current-row":""}},[a("el-table-column",{attrs:{label:"通道ID",align:"center"},scopedSlots:e._u([{key:"default",fn:function(t){return[a("span",[e._v(e._s(t.row.id))])]}}])}),e._v(" "),a("el-table-column",{attrs:{label:"通道类型",align:"center"},scopedSlots:e._u([{key:"default",fn:function(t){return[a("span",[e._v(e._s("WITHDRAW_OTC"==t.row.cashType?"提现通道":"充值通道"))])]}}])}),e._v(" "),a("el-table-column",{attrs:{label:"通道显示名称",align:"center"},scopedSlots:e._u([{key:"default",fn:function(t){return[a("span",[e._v(e._s(t.row.displayName))])]}}])}),e._v(" "),a("el-table-column",{attrs:{label:"通道显示描述",align:"center"},scopedSlots:e._u([{key:"default",fn:function(t){return[a("span",[e._v(e._s(t.row.description))])]}}])}),e._v(" "),a("el-table-column",{attrs:{label:"最小限额",align:"center"},scopedSlots:e._u([{key:"default",fn:function(t){return[a("span",[e._v(e._s(t.row.minAmount))])]}}])}),e._v(" "),a("el-table-column",{attrs:{label:"最大限额",align:"center"},scopedSlots:e._u([{key:"default",fn:function(t){return[a("span",[e._v(e._s(t.row.maxAmount))])]}}])}),e._v(" "),a("el-table-column",{attrs:{label:"通道名称",align:"center",width:"220"},scopedSlots:e._u([{key:"default",fn:function(t){return[a("span",[e._v(e._s(t.row.payApiName||"--"))])]}}])}),e._v(" "),a("el-table-column",{attrs:{label:"是否启用",align:"center"},scopedSlots:e._u([{key:"default",fn:function(t){return[a("el-switch",{on:{change:function(a){return e.changeEnable(t.row)}},model:{value:t.row.enable,callback:function(a){e.$set(t.row,"enable",a)},expression:"scope.row.enable"}})]}}])}),e._v(" "),a("el-table-column",{attrs:{fixed:"right",label:"操作",width:"100",align:"center"},scopedSlots:e._u([{key:"default",fn:function(t){return[a("el-button",{attrs:{type:"primary",size:"mini"},on:{click:function(a){return e.edit(t.row)}}},[e._v("修改")])]}}])})],1)],1),e._v(" "),a("el-pagination",{staticClass:"ygw-fenye",attrs:{background:"","current-page":e.param.page,"page-size":e.param.size,layout:"total,sizes,prev, pager, next, jumper",total:e.total},on:{"size-change":e.handleSizeChange,"current-change":e.handleCurrentChange,"update:currentPage":function(t){return e.$set(e.param,"page",t)},"update:current-page":function(t){return e.$set(e.param,"page",t)}}}),e._v(" "),a("el-dialog",{attrs:{visible:e.showEdit},on:{"update:visible":function(t){e.showEdit=t},closed:e.dialogClose}},[a("el-form",{ref:"form",attrs:{model:e.form,"label-width":"80px"}},[a("el-form-item",{attrs:{label:"通道类型"}},[a("span",[e._v(e._s("WITHDRAW_OTC"==e.form.cashType?"提现":"充值")+"通道")])]),e._v(" "),e._l(e.form.languageList.filter((function(e){return"id_ID"==e.language})),(function(t){return[a("el-form-item",{attrs:{label:"通道名称"}},[a("el-input",{model:{value:t.displayName,callback:function(a){e.$set(t,"displayName",a)},expression:"item.displayName"}})],1),e._v(" "),a("el-form-item",{attrs:{label:"通道描述"}},[a("el-input",{model:{value:t.description,callback:function(a){e.$set(t,"description",a)},expression:"item.description"}})],1)]})),e._v(" "),a("el-form-item",{attrs:{label:"logo名称"}},[a("el-input",{model:{value:e.form.logo,callback:function(t){e.$set(e.form,"logo",t)},expression:"form.logo"}})],1),e._v(" "),a("el-form-item",{attrs:{label:"最小限额"}},[a("el-input",{model:{value:e.form.minAmount,callback:function(t){e.$set(e.form,"minAmount",t)},expression:"form.minAmount"}})],1),e._v(" "),a("el-form-item",{attrs:{label:"最大限额"}},[a("el-input",{model:{value:e.form.maxAmount,callback:function(t){e.$set(e.form,"maxAmount",t)},expression:"form.maxAmount"}})],1),e._v(" "),a("el-form-item",[a("el-button",{attrs:{type:"primary"},on:{click:e.onSubmit}},[e._v("确定")]),e._v(" "),a("el-button",{on:{click:e.dialogClose}},[e._v("取消")])],1)],2)],1)],1)},l=[],i=(a("386d"),a("456d"),a("ac6a"),a("c5f6"),a("db72")),o=a("2f62"),s={data:function(){return{loading:!1,showEdit:!1,isLoading:!1,total:0,brokerData:[],tableData:[],param:{cashType:"",page:1,size:20},cashList:[{id:0,name:"全部",type:""},{id:1,name:"充值",type:"PAYMENT_OTC"},{id:2,name:"提现",type:"WITHDRAW_OTC"}],form:{id:0,cashType:"",country:"",enable:!0,exchangeRateList:[],languageList:[],needKyc:!0,logo:"",maxAmount:0,minAmount:0,payApiId:0,payApiName:"",payMethodId:0,payMethodName:""}}},mounted:function(){this.getList()},methods:Object(i["a"])(Object(i["a"])({},Object(o["b"])("system",{queryDisplayList:"queryDisplayList",updateEnable:"updateEnable",updatePayment:"updatePayment"})),{},{getList:function(){var e=this;this.isLoading=!0,this.queryDisplayList(this.param).then((function(t){console.log(t),t&&0==t.code&&(e.tableData=t.data.records||[],e.total=Number(t.data.total)),e.isLoading=!1})).catch((function(t){e.tableData=[],e.isLoading=!1}))},onAudit:function(e){var t=this,a={id:e.id,status:"WORKING"};this.orderaudit(a).then((function(e){t.getList()}))},handleCurrentChange:function(e){this.param.page=e,this.getList()},handleSizeChange:function(e){this.param.size=e,this.getList()},search:function(){this.param.page=1,this.getList()},changeEnable:function(e){this.updateEnable({id:e.id,enable:e.enable})},edit:function(e){var t=this;this.showEdit=!0,Object.keys(this.form).forEach((function(a){t.form[a]=e[a]}))},dialogClose:function(){this.showEdit=!1},onSubmit:function(){var e=this;this.updatePayment(this.form).then((function(t){e.search(),e.dialogClose()}))}})},r=s,u=(a("500f"),a("2877")),c=Object(u["a"])(r,n,l,!1,null,"0b63d403",null);t["default"]=c.exports},"386d":function(e,t,a){"use strict";var n=a("cb7c"),l=a("83a1"),i=a("5f1b");a("214f")("search",1,(function(e,t,a,o){return[function(a){var n=e(this),l=void 0==a?void 0:a[t];return void 0!==l?l.call(a,n):new RegExp(a)[t](String(n))},function(e){var t=o(a,e,this);if(t.done)return t.value;var s=n(e),r=String(this),u=s.lastIndex;l(u,0)||(s.lastIndex=0);var c=i(s,r);return l(s.lastIndex,u)||(s.lastIndex=u),null===c?-1:c.index}]}))},"500f":function(e,t,a){"use strict";a("60b5")},"60b5":function(e,t,a){},"83a1":function(e,t){e.exports=Object.is||function(e,t){return e===t?0!==e||1/e===1/t:e!=e&&t!=t}}}]);