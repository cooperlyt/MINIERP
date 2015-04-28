(function(D,A){A.utils=A.utils||{};
A.utils.addCSSText=function(H,F){var G=D("<style></style>").attr({type:"text/css",id:F}).appendTo("head");
try{G.html(H)
}catch(I){G[0].styleSheet.cssText=H
}};
A.utils.Ranges=function(){this.ranges=[]
};
A.utils.Ranges.prototype={add:function(F){var G=0;
while(G<this.ranges.length&&F>=this.ranges[G++][1]){}G--;
if(this.ranges[G-1]&&F==(this.ranges[G-1][1]+1)){if(F==(this.ranges[G][0]-1)){this.ranges[G-1][1]=this.ranges[G][1];
this.ranges.splice(G,1)
}else{this.ranges[G-1][1]++
}}else{if(this.ranges[G]){if(this.ranges[G]&&F==(this.ranges[G][0]-1)){this.ranges[G][0]--
}else{if(F==(this.ranges[G][1]+1)){this.ranges[G][1]++
}else{if(F<this.ranges[G][1]){this.ranges.splice(G,0,[F,F])
}else{this.ranges.splice(G+1,0,[F,F])
}}}}else{this.ranges.splice(G,0,[F,F])
}}},remove:function(F){var G=0;
while(G<this.ranges.length&&F>this.ranges[G++][1]){}G--;
if(this.ranges[G]){if(F==(this.ranges[G][1])){if(F==(this.ranges[G][0])){this.ranges.splice(G,1)
}else{this.ranges[G][1]--
}}else{if(F==(this.ranges[G][0])){this.ranges[G][0]++
}else{this.ranges.splice(G+1,0,[F+1,this.ranges[G][1]]);
this.ranges[G][1]=F-1
}}}},clear:function(){this.ranges=[]
},contains:function(F){var G=0;
while(G<this.ranges.length&&F>=this.ranges[G][0]){if(F>=this.ranges[G][0]&&F<=this.ranges[G][1]){return true
}else{G++
}}return false
},toString:function(){var F=new Array(this.ranges.length);
for(var G=0;
G<this.ranges.length;
G++){F[G]=this.ranges[G].join()
}return F.join(";")
}};
var B="rf-edt-c-";
var C=20;
A.ui=A.ui||{};
A.ui.ExtendedDataTable=A.BaseComponent.extendClass({name:"ExtendedDataTable",init:function(K,H,G,I){E.constructor.call(this,K);
this.ranges=new A.utils.Ranges();
this.rowCount=H;
this.ajaxFunction=G;
this.options=I||{};
this.element=this.attachToDom();
this.newWidths={};
this.storeDomReferences();
if(this.options.onready&&typeof this.options.onready=="function"){A.Event.bind(this.element,"rich:ready",this.options.onready)
}this.resizeEventName="resize.rf.edt."+this.id;
D(document).ready(D.proxy(this.initialize,this));
this.activateResizeListener();
var F=D(this.element).find(".rf-edt-b .rf-edt-cnt");
var J=function(M,L){return function(){setTimeout(function(){M.scrollElement.scrollLeft=L.scrollLeft();
M.updateScrollPosition()
},0)
}
};
F.bind("scroll",J(this,F));
D(this.scrollElement).bind("scroll",D.proxy(this.updateScrollPosition,this));
this.bindHeaderHandlers();
D(this.element).bind("rich:onajaxcomplete",D.proxy(this.ajaxComplete,this));
this.resizeData={};
this.idOfReorderingColumn="";
this.timeoutId=null
},storeDomReferences:function(){this.dragElement=document.getElementById(this.id+":d");
this.reorderElement=document.getElementById(this.id+":r");
this.reorderMarkerElement=document.getElementById(this.id+":rm");
this.widthInput=document.getElementById(this.id+":wi");
this.selectionInput=document.getElementById(this.id+":si");
this.header=D(this.element).children(".rf-edt-hdr");
this.headerCells=this.header.find(".rf-edt-hdr-c");
this.footerCells=D(this.element).children(".rf-edt-ftr").find(".rf-edt-ftr-c");
this.resizerHolders=this.header.find(".rf-edt-rsz-cntr");
this.frozenHeaderPartElement=document.getElementById(this.id+":frozenHeader");
this.frozenColumnCount=this.frozenHeaderPartElement?this.frozenHeaderPartElement.children[0].rows[0].cells.length:0;
this.headerElement=document.getElementById(this.id+":header");
this.footerElement=document.getElementById(this.id+":footer");
this.scrollElement=document.getElementById(this.id+":scrl");
this.scrollContentElement=document.getElementById(this.id+":scrl-cnt")
},getColumnPosition:function(H){var F;
for(var G=0;
G<this.headerCells.length;
G++){if(H==this.headerCells[G].className.match(new RegExp(B+"([^\\W]*)"))[1]){F=G
}}return F
},setColumnPosition:function(K,F){var J="";
var H;
for(var G=0;
G<this.headerCells.length;
G++){var I=this.headerCells[G].className.match(new RegExp(B+"([^\\W]*)"))[1];
if(G==F){if(H){J+=I+","+K+","
}else{J+=K+","+I+","
}}else{if(K!=I){J+=I+","
}else{H=true
}}}this.ajaxFunction(null,{"rich:columnsOrder":J})
},setColumnWidth:function(I,G){G=G+"px";
var F=D(document.getElementById(this.element.id));
F.find("."+B+I).parent().css("width",G);
F.find("."+B+I).css("width",G);
this.newWidths[I]=G;
var H=new Array();
for(var J in this.newWidths){H.push(J+":"+this.newWidths[J])
}this.widthInput.value=H.toString();
this.updateLayout();
this.adjustResizers();
this.ajaxFunction()
},filter:function(H,I,F){if(typeof (I)=="undefined"||I==null){I=""
}var G={};
G[this.id+"rich:filtering"]=H+":"+I+":"+F;
this.ajaxFunction(null,G)
},clearFiltering:function(){this.filter("","",true)
},sortHandler:function(I){var F=D(I.data.sortHandle);
var G=F.find(".rf-edt-srt-btn");
var J=G.data("columnid");
var H=G.hasClass("rf-edt-srt-asc")?"descending":"ascending";
this.sort(J,H,false)
},filterHandler:function(G){var F=D(G.data.filterHandle);
var H=F.data("columnid");
var I=F.val();
this.filter(H,I,false)
},sort:function(I,G,F){if(typeof (G)=="string"){G=G.toLowerCase()
}var H={};
H[this.id+"rich:sorting"]=I+":"+G+":"+F;
this.ajaxFunction(null,H)
},clearSorting:function(){this.sort("","",true)
},destroy:function(){D(window).unbind("resize",this.updateLayout);
D(A.getDomElement(this.id+":st")).remove();
E.destroy.call(this)
},bindHeaderHandlers:function(){this.header.find(".rf-edt-rsz").bind("mousedown",D.proxy(this.beginResize,this));
this.headerCells.bind("mousedown",D.proxy(this.beginReorder,this));
var F=this;
this.header.find(".rf-edt-c-srt").each(function(){D(this).bind("click",{sortHandle:this},D.proxy(F.sortHandler,F))
});
this.header.find(".rf-edt-flt-i").each(function(){D(this).bind("blur",{filterHandle:this},D.proxy(F.filterHandler,F))
})
},updateLayout:function(){this.deActivateResizeListener();
this.headerCells.height("auto");
var L=0;
this.headerCells.each(function(){if(this.clientHeight>L){L=this.clientHeight
}});
this.headerCells.height(L+"px");
this.footerCells.height("auto");
var H=0;
this.footerCells.each(function(){if(this.clientHeight>H){H=this.clientHeight
}});
this.footerCells.height(H+"px");
this.contentDivElement.css("width","auto");
var K=this.frozenHeaderPartElement?this.frozenHeaderPartElement.offsetWidth:0;
var J=Math.max(0,this.element.clientWidth-K);
if(J){this.parts.each(function(){this.style.width="auto"
});
var G=this.parts.width();
if(G>J){this.contentDivElement.css("width",J+"px")
}this.contentDivElement.css("display","block");
if(G>J){this.parts.each(function(){this.style.width=J+"px"
});
this.scrollElement.style.display="block";
this.scrollElement.style.overflowX="scroll";
this.scrollElement.style.width=J+"px";
this.scrollContentElement.style.width=G+"px";
this.updateScrollPosition()
}else{this.parts.each(function(){this.style.width=""
});
this.scrollElement.style.display="none"
}}else{this.contentDivElement.css("display","none")
}var F=this.element.clientHeight;
var I=this.element.firstChild;
while(I&&(!I.nodeName||I.nodeName.toUpperCase()!="TABLE")){if(I.nodeName&&I.nodeName.toUpperCase()=="DIV"&&I!=this.bodyElement){F-=I.offsetHeight
}I=I.nextSibling
}if(this.bodyElement.offsetHeight>F||!this.contentElement){this.bodyElement.style.height=F+"px"
}this.activateResizeListener()
},adjustResizers:function(){var H=this.scrollElement?this.scrollElement.scrollLeft:0;
var G=this.element.clientWidth-3;
var F=0;
for(;
F<this.frozenColumnCount;
F++){if(G>0){this.resizerHolders[F].style.display="none";
this.resizerHolders[F].style.display="";
G-=this.resizerHolders[F].offsetWidth
}if(G<=0){this.resizerHolders[F].style.display="none"
}}H-=3;
for(;
F<this.resizerHolders.length;
F++){if(G>0){this.resizerHolders[F].style.display="none";
if(H>0){this.resizerHolders[F].style.display="";
H-=this.resizerHolders[F].offsetWidth;
if(H>0){this.resizerHolders[F].style.display="none"
}else{G+=H
}}else{this.resizerHolders[F].style.display="";
G-=this.resizerHolders[F].offsetWidth
}}if(G<=0){this.resizerHolders[F].style.display="none"
}}},updateScrollPosition:function(){if(this.scrollElement){var F=this.scrollElement.scrollLeft;
this.parts.each(function(){this.scrollLeft=F
})
}this.adjustResizers()
},initialize:function(){this.deActivateResizeListener();
if(!D(this.element).is(":visible")){this.showOffscreen(this.element)
}this.bodyElement=document.getElementById(this.id+":b");
this.bodyElement.tabIndex=-1;
this.contentDivElement=D(this.bodyElement).find(".rf-edt-cnt");
var F=D(this.bodyElement);
this.contentElement=F.children("div:not(.rf-edt-ndt):first")[0];
if(this.contentElement){this.spacerElement=this.contentElement.children[0];
this.dataTableElement=this.contentElement.lastChild;
this.tbodies=D(document.getElementById(this.id+":tbf")).add(document.getElementById(this.id+":tbn"));
this.rows=this.tbodies[0].rows.length;
this.rowHeight=this.dataTableElement.offsetHeight/this.rows;
if(this.rowCount!=this.rows){this.contentElement.style.height=(this.rowCount*this.rowHeight)+"px"
}F.bind("scroll",D.proxy(this.bodyScrollListener,this));
if(this.options.selectionMode!="none"){this.tbodies.bind("click",D.proxy(this.selectionClickListener,this));
F.bind(window.opera?"keypress":"keydown",D.proxy(this.selectionKeyDownListener,this));
this.initializeSelection()
}}else{this.spacerElement=null;
this.dataTableElement=null
}var G=this.element;
this.parts=D(this.element).find(".rf-edt-cnt, .rf-edt-ftr-cnt").filter(function(){return D(this).parents(".rf-edt").get(0)===G
});
this.updateLayout();
this.updateScrollPosition();
if(D(this.element).data("offscreenElements")){this.hideOffscreen(this.element)
}this.activateResizeListener();
D(this.element).trigger("rich:ready",this)
},showOffscreen:function(G){var F=D(G);
var I=F.parents(":not(:visible)").addBack().toArray().reverse();
var H=this;
D.each(I,function(){$this=D(this);
if($this.css("display")==="none"){H.showOffscreenElement(D(this))
}});
F.data("offscreenElements",I)
},hideOffscreen:function(G){var F=D(G);
var I=F.data("offscreenElements");
var H=this;
D.each(I,function(){$this=D(this);
if($this.data("offscreenOldValues")){H.hideOffscreenElement(D(this))
}});
F.removeData("offscreenElements")
},showOffscreenElement:function(F){var G={};
G.oldPosition=F.css("position");
G.oldLeft=F.css("left");
G.oldDisplay=F.css("display");
F.css("position","absolute");
F.css("left","-10000");
F.css("display","block");
F.data("offscreenOldValues",G)
},hideOffscreenElement:function(F){var G=F.data("offscreenOldValues");
F.css("display",G.oldDisplay);
F.css("left",G.oldLeft);
F.css("position",G.oldPosition);
F.removeData("offscreenOldValues")
},drag:function(F){D(this.dragElement).setPosition({left:Math.max(this.resizeData.left+C,F.pageX)});
return false
},beginResize:function(F){var G=F.currentTarget.parentNode.className.match(new RegExp(B+"([^\\W]*)"))[1];
this.resizeData={id:G,left:D(F.currentTarget).parent().offset().left};
this.dragElement.style.height=this.element.offsetHeight+"px";
D(this.dragElement).setPosition({top:D(this.element).offset().top,left:F.pageX});
this.dragElement.style.display="block";
D(document).bind("mousemove",D.proxy(this.drag,this));
D(document).one("mouseup",D.proxy(this.endResize,this));
return false
},endResize:function(G){D(document).unbind("mousemove",this.drag);
this.dragElement.style.display="none";
var F=Math.max(C,G.pageX-this.resizeData.left);
this.setColumnWidth(this.resizeData.id,F)
},reorder:function(F){D(this.reorderElement).setPosition(F,{offset:[5,5]});
this.reorderElement.style.display="block";
return false
},beginReorder:function(F){if(!D(F.target).is("a, img, :input")){this.idOfReorderingColumn=F.currentTarget.className.match(new RegExp(B+"([^\\W]*)"))[1];
D(document).bind("mousemove",D.proxy(this.reorder,this));
this.headerCells.bind("mouseover",D.proxy(this.overReorder,this));
D(document).one("mouseup",D.proxy(this.cancelReorder,this));
return false
}},overReorder:function(G){if(this.idOfReorderingColumn!=G.currentTarget.className.match(new RegExp(B+"([^\\W]*)"))[1]){var F=D(G.currentTarget);
var H=F.offset();
D(this.reorderMarkerElement).setPosition({top:H.top+F.height(),left:H.left-5});
this.reorderMarkerElement.style.display="block";
F.one("mouseout",D.proxy(this.outReorder,this));
F.one("mouseup",D.proxy(this.endReorder,this))
}},outReorder:function(F){this.reorderMarkerElement.style.display="";
D(F.currentTarget).unbind("mouseup",this.endReorder)
},endReorder:function(F){this.reorderMarkerElement.style.display="";
D(F.currentTarget).unbind("mouseout",this.outReorder);
var I=F.currentTarget.className.match(new RegExp(B+"([^\\W]*)"))[1];
var H="";
var G=this;
this.headerCells.each(function(){var J=this.className.match(new RegExp(B+"([^\\W]*)"))[1];
if(J==I){H+=G.idOfReorderingColumn+","+I+","
}else{if(J!=G.idOfReorderingColumn){H+=J+","
}}});
this.ajaxFunction(F,{"rich:columnsOrder":H})
},cancelReorder:function(F){D(document).unbind("mousemove",this.reorder);
this.headerCells.unbind("mouseover",this.overReorder);
this.reorderElement.style.display="none"
},loadData:function(G){var F=Math.round((this.bodyElement.scrollTop+this.bodyElement.clientHeight/2)/this.rowHeight-this.rows/2);
if(F<=0){F=0
}else{F=Math.min(this.rowCount-this.rows,F)
}this.ajaxFunction(G,{"rich:clientFirst":F})
},bodyScrollListener:function(F){if(this.timeoutId){window.clearTimeout(this.timeoutId);
this.timeoutId=null
}if(Math.max(F.currentTarget.scrollTop-this.rowHeight,0)<this.spacerElement.offsetHeight||Math.min(F.currentTarget.scrollTop+this.rowHeight+F.currentTarget.clientHeight,F.currentTarget.scrollHeight)>this.spacerElement.offsetHeight+this.dataTableElement.offsetHeight){var G=this;
this.timeoutId=window.setTimeout(function(H){G.loadData(H)
},1000)
}},showActiveRow:function(){if(this.bodyElement.scrollTop>this.activeIndex*this.rowHeight+this.spacerElement.offsetHeight){this.bodyElement.scrollTop=Math.max(this.bodyElement.scrollTop-this.rowHeight,0)
}else{if(this.bodyElement.scrollTop+this.bodyElement.clientHeight<(this.activeIndex+1)*this.rowHeight+this.spacerElement.offsetHeight){this.bodyElement.scrollTop=Math.min(this.bodyElement.scrollTop+this.rowHeight,this.bodyElement.scrollHeight-this.bodyElement.clientHeight)
}}},selectRow:function(F){this.ranges.add(F);
for(var G=0;
G<this.tbodies.length;
G++){D(this.tbodies[G].rows[F]).addClass("rf-edt-r-sel")
}},deselectRow:function(F){this.ranges.remove(F);
for(var G=0;
G<this.tbodies.length;
G++){D(this.tbodies[G].rows[F]).removeClass("rf-edt-r-sel")
}},setActiveRow:function(F){if(typeof this.activeIndex=="number"){for(var G=0;
G<this.tbodies.length;
G++){D(this.tbodies[G].rows[this.activeIndex]).removeClass("rf-edt-r-act")
}}this.activeIndex=F;
for(var G=0;
G<this.tbodies.length;
G++){D(this.tbodies[G].rows[this.activeIndex]).addClass("rf-edt-r-act")
}},resetShiftRow:function(){if(typeof this.shiftIndex=="number"){for(var F=0;
F<this.tbodies.length;
F++){D(this.tbodies[F].rows[this.shiftIndex]).removeClass("rf-edt-r-sht")
}}this.shiftIndex=null
},setShiftRow:function(F){this.resetShiftRow();
this.shiftIndex=F;
if(typeof F=="number"){for(var G=0;
G<this.tbodies.length;
G++){D(this.tbodies[G].rows[this.shiftIndex]).addClass("rf-edt-r-sht")
}}},initializeSelection:function(){this.ranges.clear();
var F=this.selectionInput.value.split("|");
this.activeIndex=F[1]||null;
this.shiftIndex=F[2]||null;
this.selectionFlag=null;
var H=this.tbodies[0].rows;
for(var G=0;
G<H.length;
G++){var I=D(H[G]);
if(I.hasClass("rf-edt-r-sel")){this.ranges.add(I[0].rowIndex)
}if(I.hasClass("rf-edt-r-act")){this.activeIndex=I[0].rowIndex
}if(I.hasClass("rf-edt-r-sht")){this.shiftIndex=I[0].rowIndex
}}this.writeSelection()
},writeSelection:function(){this.selectionInput.value=[this.ranges,this.activeIndex,this.shiftIndex,this.selectionFlag].join("|")
},selectRows:function(F){if(typeof F=="number"){F=[F,F]
}var H;
var G=0;
for(;
G<F[0];
G++){if(this.ranges.contains(G)){this.deselectRow(G);
H=true
}}for(;
G<=F[1];
G++){if(!this.ranges.contains(G)){this.selectRow(G);
H=true
}}for(;
G<this.rows;
G++){if(this.ranges.contains(G)){this.deselectRow(G);
H=true
}}this.selectionFlag=typeof this.shiftIndex=="string"?this.shiftIndex:"x";
return H
},processSlectionWithShiftKey:function(G){if(this.shiftIndex==null){this.setShiftRow(this.activeIndex!=null?this.activeIndex:G)
}var F;
if("u"==this.shiftIndex){F=[0,G]
}else{if("d"==this.shiftIndex){F=[G,this.rows-1]
}else{if(G>=this.shiftIndex){F=[this.shiftIndex,G]
}else{F=[G,this.shiftIndex]
}}}return this.selectRows(F)
},onbeforeselectionchange:function(F){return !this.options.onbeforeselectionchange||this.options.onbeforeselectionchange.call(this.element,F)!=false
},onselectionchange:function(G,F,H){if(!G.shiftKey){this.resetShiftRow()
}if(this.activeIndex!=F){this.setActiveRow(F);
this.showActiveRow()
}if(H){this.writeSelection();
if(this.options.onselectionchange){this.options.onselectionchange.call(this.element,G)
}}},selectionClickListener:function(G){if(!this.onbeforeselectionchange(G)){return 
}var I;
if(G.shiftKey||G.ctrlKey){if(window.getSelection){window.getSelection().removeAllRanges()
}else{if(document.selection){document.selection.empty()
}}}var H=G.target;
while(this.tbodies.index(H.parentNode)==-1){H=H.parentNode
}var F=H.rowIndex;
if(typeof (F)==="undefined"){return 
}if(this.options.selectionMode=="single"||(this.options.selectionMode!="multipleKeyboardFree"&&!G.shiftKey&&!G.ctrlKey)){I=this.selectRows(F)
}else{if(this.options.selectionMode=="multipleKeyboardFree"||(!G.shiftKey&&G.ctrlKey)){if(this.ranges.contains(F)){this.deselectRow(F)
}else{this.selectRow(F)
}I=true
}else{I=this.processSlectionWithShiftKey(F)
}}this.onselectionchange(G,F,I)
},selectionKeyDownListener:function(G){if(G.ctrlKey&&this.options.selectionMode!="single"&&(G.keyCode==65||G.keyCode==97)&&this.onbeforeselectionchange(G)){this.selectRows([0,this.rows]);
this.selectionFlag="a";
this.onselectionchange(G,this.activeIndex,true);
G.preventDefault()
}else{var F;
if(G.keyCode==38){F=-1
}else{if(G.keyCode==40){F=1
}}if(F!=null&&this.onbeforeselectionchange(G)){if(typeof this.activeIndex=="number"){F+=this.activeIndex;
if(F>=0&&F<this.rows){var H;
if(this.options.selectionMode=="single"||(!G.shiftKey&&!G.ctrlKey)){H=this.selectRows(F)
}else{if(G.shiftKey){H=this.processSlectionWithShiftKey(F)
}}this.onselectionchange(G,F,H)
}}G.preventDefault()
}}},ajaxComplete:function(H,I){this.storeDomReferences();
if(I.reinitializeHeader){this.bindHeaderHandlers();
this.updateLayout()
}else{this.selectionInput=document.getElementById(this.id+":si");
if(I.reinitializeBody){this.rowCount=I.rowCount;
this.initialize()
}else{if(this.options.selectionMode!="none"){this.initializeSelection()
}}if(this.spacerElement){this.spacerElement.style.height=(I.first*this.rowHeight)+"px"
}}var F=D(document.getElementById(this.element.id)),G=new Array();
for(var J in this.newWidths){F.find("."+B+J).css("width",this.newWidths[J]).parent().css("width",this.newWidths[J]);
G.push(J+":"+this.newWidths[J])
}this.widthInput.value=G.toString();
this.updateLayout();
this.adjustResizers()
},activateResizeListener:function(){if(typeof this.resizeEventName!=="undefined"){D(window).on(this.resizeEventName,D.proxy(this.updateLayout,this))
}},deActivateResizeListener:function(){if(typeof this.resizeEventName!=="undefined"){D(window).off(this.resizeEventName)
}},contextMenuAttach:function(G){var F="[id='"+this.element.id+"'] ";
F+=(typeof G.options.targetSelector==="undefined")?".rf-edt-b td":G.options.targetSelector;
F=D.trim(F);
A.Event.bind(F,G.options.showEvent,D.proxy(G.__showHandler,G),G)
},contextMenuShow:function(I,G){var H=G.target;
while(this.tbodies.index(H.parentNode)==-1){H=H.parentNode
}var F=H.rowIndex;
if(!this.ranges.contains(F)){this.selectionClickListener(G)
}}});
var E=A.ui.ExtendedDataTable.$super
}(RichFaces.jQuery,window.RichFaces));