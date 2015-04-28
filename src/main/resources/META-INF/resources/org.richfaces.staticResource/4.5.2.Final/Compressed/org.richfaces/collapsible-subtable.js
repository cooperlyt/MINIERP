(function(B,A){A.ui=A.ui||{};
A.ui.CollapsibleSubTable=function(H,G,F){this.id=H;
this.options=B.extend(this.options,F||{});
this.stateInput=F.stateInput;
this.optionsInput=F.optionsInput;
this.expandMode=F.expandMode||A.ui.CollapsibleSubTable.MODE_CLNT;
this.eventOptions=F.eventOptions;
this.formId=G;
this.isNested=F.isNested;
if(!this.isNested){var D=this;
var E=B(document.getElementById(this.id)).parent();
E.find(".rf-dt-c-srt").each(function(){B(this).bind("click",{sortHandle:this},B.proxy(D.sortHandler,D))
});
E.find(".rf-dt-flt-i").each(function(){B(this).bind("blur",{filterHandle:this},B.proxy(D.filterHandler,D))
})
}this.attachToDom()
};
B.extend(A.ui.CollapsibleSubTable,{MODE_AJAX:"ajax",MODE_SRV:"server",MODE_CLNT:"client",collapse:0,expand:1,SORTING:"rich:sorting",FILTERING:"rich:filtering"});
A.BaseComponent.extend(A.ui.CollapsibleSubTable);
var C=A.ui.CollapsibleSubTable.$super;
B.extend(A.ui.CollapsibleSubTable.prototype,(function(){var E=function(M,L){A.ajax(this.id,M,{parameters:L})
};
var I=function(P,R,N,M){var Q={};
var O=this.id+P;
Q[O]=(R+":"+(N||"")+":"+M);
var L=this.options.ajaxEventOption;
for(O in L){if(!Q[O]){Q[O]=L[O]
}}return Q
};
var F=function(){if(!this.isNested){return B(document.getElementById(this.id)).parent()
}else{var L=new RegExp("^"+this.id+"\\:\\d+\\:b$");
return B(document.getElementById(this.id)).parent().find("tr").filter(function(){return this.id.match(L)
})
}};
var G=function(){return B(document.getElementById(this.stateInput))
};
var K=function(){return B(document.getElementById(this.optionsInput))
};
var H=function(M,L){this.__switchState();
A.ajax(this.id,M,L)
};
var J=function(L){this.__switchState();
B(document.getElementById(this.formId)).submit()
};
var D=function(L){if(this.isExpanded()){this.collapse(L)
}else{this.expand(L)
}};
return{name:"CollapsibleSubTable",sort:function(M,N,L){E.call(this,null,I.call(this,A.ui.CollapsibleSubTable.SORTING,M,N,L))
},clearSorting:function(){this.sort("","",true)
},sortHandler:function(O){var L=B(O.data.sortHandle);
var M=L.find(".rf-dt-srt-btn");
var P=M.data("columnid");
var N=M.hasClass("rf-dt-srt-asc")?"descending":"ascending";
this.sort(P,N,false)
},filter:function(M,N,L){E.call(this,null,I.call(this,A.ui.CollapsibleSubTable.FILTERING,M,N,L))
},clearFiltering:function(){this.filter("","",true)
},filterHandler:function(M){var L=B(M.data.filterHandle);
var N=L.data("columnid");
var O=L.val();
this.filter(N,O,false)
},switchState:function(M,L){if(this.expandMode==A.ui.CollapsibleSubTable.MODE_AJAX){H.call(this,M,this.eventOptions,L)
}else{if(this.expandMode==A.ui.CollapsibleSubTable.MODE_SRV){J.call(this,L)
}else{if(this.expandMode==A.ui.CollapsibleSubTable.MODE_CLNT){D.call(this,L)
}}}},collapse:function(L){if(this.isNested){var M=new RegExp("^"+this.id+"\\:\\d+\\:\\w+\\:expanded$");
var O=new RegExp("^"+this.id+"\\:\\d+\\:\\w+\\:collapsed$");
var N=new RegExp("^"+this.id+"\\:\\d+\\:\\w+$");
B(document.getElementById(this.id)).parent().find("tr[style='display: none;']").filter(function(){return this.id.match(N)
}).each(function(){if(this.rf){if(this.rf.component.isExpanded){B(document.getElementById(this.id)).parent().find(".rf-csttg-exp").filter(function(){return this.id.match(M)
}).each(function(){B(this).hide()
});
B(document.getElementById(this.id)).parent().find(".rf-csttg-colps").filter(function(){return this.id.match(O)
}).each(function(){B(this).show()
});
this.rf.component.collapse()
}}})
}this.setState(A.ui.CollapsibleSubTable.collapse);
F.call(this).hide()
},expand:function(L){this.setState(A.ui.CollapsibleSubTable.expand);
F.call(this).show()
},isExpanded:function(){return(parseInt(this.getState())==A.ui.CollapsibleSubTable.expand)
},__switchState:function(L){var M=this.isExpanded()?A.ui.CollapsibleSubTable.collapse:A.ui.CollapsibleSubTable.expand;
this.setState(M)
},getState:function(){return G.call(this).val()
},setState:function(L){G.call(this).val(L)
},setOption:function(L){K.call(this).val(L)
},getMode:function(){return this.expandMode
},destroy:function(){C.destroy.call(this)
}}
})())
})(RichFaces.jQuery,window.RichFaces);