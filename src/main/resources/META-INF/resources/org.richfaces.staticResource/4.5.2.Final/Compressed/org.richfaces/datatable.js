(function(B,A){A.ui=A.ui||{};
A.ui.DataTable=function(G,E){C.constructor.call(this,G);
this.options=B.extend(this.options,E||{});
this.element=this.attachToDom();
var D=this;
var F=B(this.element).find(".rf-dt-thd");
F.find(".rf-dt-c-srt").each(function(){B(this).bind("click",{sortHandle:this},B.proxy(D.sortHandler,D))
});
F.find(".rf-dt-flt-i").each(function(){B(this).bind("blur",{filterHandle:this},B.proxy(D.filterHandler,D))
});
B(this.element).trigger("rich:ready",this)
};
A.BaseComponent.extend(A.ui.DataTable);
var C=A.ui.DataTable.$super;
B.extend(A.ui.DataTable,{SORTING:"rich:sorting",FILTERING:"rich:filtering",SUBTABLE_SELECTOR:".rf-cst"});
B.extend(A.ui.DataTable.prototype,(function(){var D=function(G,F){A.ajax(this.id,G,{parameters:F})
};
var E=function(J,L,H,G){var K={};
var I=this.id+J;
K[I]=(L+":"+(H||"")+":"+G);
var F=this.options.ajaxEventOption;
for(I in F){if(!K[I]){K[I]=F[I]
}}return K
};
return{name:"RichFaces.ui.DataTable",sort:function(G,H,F){D.call(this,null,E.call(this,A.ui.DataTable.SORTING,G,H,F))
},clearSorting:function(){this.sort("","",true)
},sortHandler:function(I){var F=B(I.data.sortHandle);
var G=F.find(".rf-dt-srt-btn");
var J=G.data("columnid");
var H=G.hasClass("rf-dt-srt-asc")?"descending":"ascending";
this.sort(J,H,false)
},filter:function(G,H,F){D.call(this,null,E.call(this,A.ui.DataTable.FILTERING,G,H,F))
},clearFiltering:function(){this.filter("","",true)
},filterHandler:function(G){var F=B(G.data.filterHandle);
var H=F.data("columnid");
var I=F.val();
this.filter(H,I,false)
},expandAllSubTables:function(){this.invokeOnSubTables("expand")
},collapseAllSubTables:function(){this.invokeOnSubTables("collapse")
},switchSubTable:function(F){this.getSubTable(F).switchState()
},getSubTable:function(F){return A.component(F)
},invokeOnSubTables:function(G){var F=B(document.getElementById(this.id)).children(A.ui.DataTable.SUBTABLE_SELECTOR);
var H=this.invokeOnComponent;
F.each(function(){if(this.firstChild&&this.firstChild[A.RICH_CONTAINER]&&this.firstChild[A.RICH_CONTAINER].component){var I=this.firstChild[A.RICH_CONTAINER].component;
if(I instanceof RichFaces.ui.CollapsibleSubTable){H(I,G)
}}})
},invokeOnSubTable:function(H,G){var F=this.getSubTable(H);
this.invokeOnComponent(F,G)
},invokeOnComponent:function(F,H){if(F){var G=F[H];
if(typeof G=="function"){G.call(F)
}}},contextMenuAttach:function(G){var F="[id='"+this.element.id+"'] ";
F+=(typeof G.options.targetSelector==="undefined")?".rf-dt-b td":G.options.targetSelector;
F=B.trim(F);
A.Event.bind(F,G.options.showEvent,B.proxy(G.__showHandler,G),G)
},destroy:function(){C.destroy.call(this)
}}
})())
})(RichFaces.jQuery,window.RichFaces);