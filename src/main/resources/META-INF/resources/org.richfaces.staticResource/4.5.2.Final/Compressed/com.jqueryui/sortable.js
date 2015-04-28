/*
 * jQuery UI Sortable 1.11.2
 * http://jqueryui.com
 *
 * Copyright 2014 jQuery Foundation and other contributors
 * Released under the MIT license.
 * http://jquery.org/license
 *
 * http://api.jqueryui.com/sortable/
 */
(function(A){if(typeof define==="function"&&define.amd){define(["jquery","./core","./mouse","./widget"],A)
}else{A(jQuery)
}}(function(A){return A.widget("ui.sortable",A.ui.mouse,{version:"1.11.2",widgetEventPrefix:"sort",ready:false,options:{appendTo:"parent",axis:false,connectWith:false,containment:false,cursor:"auto",cursorAt:false,dropOnEmpty:true,forcePlaceholderSize:false,forceHelperSize:false,grid:false,handle:false,helper:"original",items:"> *",opacity:false,placeholder:false,revert:false,scroll:true,scrollSensitivity:20,scrollSpeed:20,scope:"default",tolerance:"intersect",zIndex:1000,activate:null,beforeStop:null,change:null,deactivate:null,out:null,over:null,receive:null,remove:null,sort:null,start:null,stop:null,update:null},_isOverAxis:function(C,B,D){return(C>=B)&&(C<(B+D))
},_isFloating:function(B){return(/left|right/).test(B.css("float"))||(/inline|table-cell/).test(B.css("display"))
},_create:function(){var B=this.options;
this.containerCache={};
this.element.addClass("ui-sortable");
this.refresh();
this.floating=this.items.length?B.axis==="x"||this._isFloating(this.items[0].item):false;
this.offset=this.element.offset();
this._mouseInit();
this._setHandleClassName();
this.ready=true
},_setOption:function(B,C){this._super(B,C);
if(B==="handle"){this._setHandleClassName()
}},_setHandleClassName:function(){this.element.find(".ui-sortable-handle").removeClass("ui-sortable-handle");
A.each(this.items,function(){(this.instance.options.handle?this.item.find(this.instance.options.handle):this.item).addClass("ui-sortable-handle")
})
},_destroy:function(){this.element.removeClass("ui-sortable ui-sortable-disabled").find(".ui-sortable-handle").removeClass("ui-sortable-handle");
this._mouseDestroy();
for(var B=this.items.length-1;
B>=0;
B--){this.items[B].item.removeData(this.widgetName+"-item")
}return this
},_mouseCapture:function(D,E){var B=null,F=false,C=this;
if(this.reverting){return false
}if(this.options.disabled||this.options.type==="static"){return false
}this._refreshItems(D);
A(D.target).parents().each(function(){if(A.data(this,C.widgetName+"-item")===C){B=A(this);
return false
}});
if(A.data(D.target,C.widgetName+"-item")===C){B=A(D.target)
}if(!B){return false
}if(this.options.handle&&!E){A(this.options.handle,B).find("*").addBack().each(function(){if(this===D.target){F=true
}});
if(!F){return false
}}this.currentItem=B;
this._removeCurrentsFromItems();
return true
},_mouseStart:function(E,F,C){var D,B,G=this.options;
this.currentContainer=this;
this.refreshPositions();
this.helper=this._createHelper(E);
this._cacheHelperProportions();
this._cacheMargins();
this.scrollParent=this.helper.scrollParent();
this.offset=this.currentItem.offset();
this.offset={top:this.offset.top-this.margins.top,left:this.offset.left-this.margins.left};
A.extend(this.offset,{click:{left:E.pageX-this.offset.left,top:E.pageY-this.offset.top},parent:this._getParentOffset(),relative:this._getRelativeOffset()});
this.helper.css("position","absolute");
this.cssPosition=this.helper.css("position");
this.originalPosition=this._generatePosition(E);
this.originalPageX=E.pageX;
this.originalPageY=E.pageY;
(G.cursorAt&&this._adjustOffsetFromHelper(G.cursorAt));
this.domPosition={prev:this.currentItem.prev()[0],parent:this.currentItem.parent()[0]};
if(this.helper[0]!==this.currentItem[0]){this.currentItem.hide()
}this._createPlaceholder();
if(G.containment){this._setContainment()
}if(G.cursor&&G.cursor!=="auto"){B=this.document.find("body");
this.storedCursor=B.css("cursor");
B.css("cursor",G.cursor);
this.storedStylesheet=A("<style>*{ cursor: "+G.cursor+" !important; }</style>").appendTo(B)
}if(G.opacity){if(this.helper.css("opacity")){this._storedOpacity=this.helper.css("opacity")
}this.helper.css("opacity",G.opacity)
}if(G.zIndex){if(this.helper.css("zIndex")){this._storedZIndex=this.helper.css("zIndex")
}this.helper.css("zIndex",G.zIndex)
}if(this.scrollParent[0]!==document&&this.scrollParent[0].tagName!=="HTML"){this.overflowOffset=this.scrollParent.offset()
}this._trigger("start",E,this._uiHash());
if(!this._preserveHelperProportions){this._cacheHelperProportions()
}if(!C){for(D=this.containers.length-1;
D>=0;
D--){this.containers[D]._trigger("activate",E,this._uiHash(this))
}}if(A.ui.ddmanager){A.ui.ddmanager.current=this
}if(A.ui.ddmanager&&!G.dropBehaviour){A.ui.ddmanager.prepareOffsets(this,E)
}this.dragging=true;
this.helper.addClass("ui-sortable-helper");
this._mouseDrag(E);
return true
},_mouseDrag:function(F){var D,E,C,H,G=this.options,B=false;
this.position=this._generatePosition(F);
this.positionAbs=this._convertPositionTo("absolute");
if(!this.lastPositionAbs){this.lastPositionAbs=this.positionAbs
}if(this.options.scroll){if(this.scrollParent[0]!==document&&this.scrollParent[0].tagName!=="HTML"){if((this.overflowOffset.top+this.scrollParent[0].offsetHeight)-F.pageY<G.scrollSensitivity){this.scrollParent[0].scrollTop=B=this.scrollParent[0].scrollTop+G.scrollSpeed
}else{if(F.pageY-this.overflowOffset.top<G.scrollSensitivity){this.scrollParent[0].scrollTop=B=this.scrollParent[0].scrollTop-G.scrollSpeed
}}if((this.overflowOffset.left+this.scrollParent[0].offsetWidth)-F.pageX<G.scrollSensitivity){this.scrollParent[0].scrollLeft=B=this.scrollParent[0].scrollLeft+G.scrollSpeed
}else{if(F.pageX-this.overflowOffset.left<G.scrollSensitivity){this.scrollParent[0].scrollLeft=B=this.scrollParent[0].scrollLeft-G.scrollSpeed
}}}else{if(F.pageY-A(document).scrollTop()<G.scrollSensitivity){B=A(document).scrollTop(A(document).scrollTop()-G.scrollSpeed)
}else{if(A(window).height()-(F.pageY-A(document).scrollTop())<G.scrollSensitivity){B=A(document).scrollTop(A(document).scrollTop()+G.scrollSpeed)
}}if(F.pageX-A(document).scrollLeft()<G.scrollSensitivity){B=A(document).scrollLeft(A(document).scrollLeft()-G.scrollSpeed)
}else{if(A(window).width()-(F.pageX-A(document).scrollLeft())<G.scrollSensitivity){B=A(document).scrollLeft(A(document).scrollLeft()+G.scrollSpeed)
}}}if(B!==false&&A.ui.ddmanager&&!G.dropBehaviour){A.ui.ddmanager.prepareOffsets(this,F)
}}this.positionAbs=this._convertPositionTo("absolute");
if(!this.options.axis||this.options.axis!=="y"){this.helper[0].style.left=this.position.left+"px"
}if(!this.options.axis||this.options.axis!=="x"){this.helper[0].style.top=this.position.top+"px"
}for(D=this.items.length-1;
D>=0;
D--){E=this.items[D];
C=E.item[0];
H=this._intersectsWithPointer(E);
if(!H){continue
}if(E.instance!==this.currentContainer){continue
}if(C!==this.currentItem[0]&&this.placeholder[H===1?"next":"prev"]()[0]!==C&&!A.contains(this.placeholder[0],C)&&(this.options.type==="semi-dynamic"?!A.contains(this.element[0],C):true)){this.direction=H===1?"down":"up";
if(this.options.tolerance==="pointer"||this._intersectsWithSides(E)){this._rearrange(F,E)
}else{break
}this._trigger("change",F,this._uiHash());
break
}}this._contactContainers(F);
if(A.ui.ddmanager){A.ui.ddmanager.drag(this,F)
}this._trigger("sort",F,this._uiHash());
this.lastPositionAbs=this.positionAbs;
return false
},_mouseStop:function(D,F){if(!D){return 
}if(A.ui.ddmanager&&!this.options.dropBehaviour){A.ui.ddmanager.drop(this,D)
}if(this.options.revert){var C=this,G=this.placeholder.offset(),B=this.options.axis,E={};
if(!B||B==="x"){E.left=G.left-this.offset.parent.left-this.margins.left+(this.offsetParent[0]===document.body?0:this.offsetParent[0].scrollLeft)
}if(!B||B==="y"){E.top=G.top-this.offset.parent.top-this.margins.top+(this.offsetParent[0]===document.body?0:this.offsetParent[0].scrollTop)
}this.reverting=true;
A(this.helper).animate(E,parseInt(this.options.revert,10)||500,function(){C._clear(D)
})
}else{this._clear(D,F)
}return false
},cancel:function(){if(this.dragging){this._mouseUp({target:null});
if(this.options.helper==="original"){this.currentItem.css(this._storedCSS).removeClass("ui-sortable-helper")
}else{this.currentItem.show()
}for(var B=this.containers.length-1;
B>=0;
B--){this.containers[B]._trigger("deactivate",null,this._uiHash(this));
if(this.containers[B].containerCache.over){this.containers[B]._trigger("out",null,this._uiHash(this));
this.containers[B].containerCache.over=0
}}}if(this.placeholder){if(this.placeholder[0].parentNode){this.placeholder[0].parentNode.removeChild(this.placeholder[0])
}if(this.options.helper!=="original"&&this.helper&&this.helper[0].parentNode){this.helper.remove()
}A.extend(this,{helper:null,dragging:false,reverting:false,_noFinalSort:null});
if(this.domPosition.prev){A(this.domPosition.prev).after(this.currentItem)
}else{A(this.domPosition.parent).prepend(this.currentItem)
}}return this
},serialize:function(D){var B=this._getItemsAsjQuery(D&&D.connected),C=[];
D=D||{};
A(B).each(function(){var E=(A(D.item||this).attr(D.attribute||"id")||"").match(D.expression||(/(.+)[\-=_](.+)/));
if(E){C.push((D.key||E[1]+"[]")+"="+(D.key&&D.expression?E[1]:E[2]))
}});
if(!C.length&&D.key){C.push(D.key+"=")
}return C.join("&")
},toArray:function(D){var B=this._getItemsAsjQuery(D&&D.connected),C=[];
D=D||{};
B.each(function(){C.push(A(D.item||this).attr(D.attribute||"id")||"")
});
return C
},_intersectsWith:function(M){var D=this.positionAbs.left,C=D+this.helperProportions.width,K=this.positionAbs.top,J=K+this.helperProportions.height,E=M.left,B=E+M.width,N=M.top,I=N+M.height,O=this.offset.click.top,H=this.offset.click.left,G=(this.options.axis==="x")||((K+O)>N&&(K+O)<I),L=(this.options.axis==="y")||((D+H)>E&&(D+H)<B),F=G&&L;
if(this.options.tolerance==="pointer"||this.options.forcePointerForContainers||(this.options.tolerance!=="pointer"&&this.helperProportions[this.floating?"width":"height"]>M[this.floating?"width":"height"])){return F
}else{return(E<D+(this.helperProportions.width/2)&&C-(this.helperProportions.width/2)<B&&N<K+(this.helperProportions.height/2)&&J-(this.helperProportions.height/2)<I)
}},_intersectsWithPointer:function(D){var E=(this.options.axis==="x")||this._isOverAxis(this.positionAbs.top+this.offset.click.top,D.top,D.height),C=(this.options.axis==="y")||this._isOverAxis(this.positionAbs.left+this.offset.click.left,D.left,D.width),G=E&&C,B=this._getDragVerticalDirection(),F=this._getDragHorizontalDirection();
if(!G){return false
}return this.floating?(((F&&F==="right")||B==="down")?2:1):(B&&(B==="down"?2:1))
},_intersectsWithSides:function(E){var C=this._isOverAxis(this.positionAbs.top+this.offset.click.top,E.top+(E.height/2),E.height),D=this._isOverAxis(this.positionAbs.left+this.offset.click.left,E.left+(E.width/2),E.width),B=this._getDragVerticalDirection(),F=this._getDragHorizontalDirection();
if(this.floating&&F){return((F==="right"&&D)||(F==="left"&&!D))
}else{return B&&((B==="down"&&C)||(B==="up"&&!C))
}},_getDragVerticalDirection:function(){var B=this.positionAbs.top-this.lastPositionAbs.top;
return B!==0&&(B>0?"down":"up")
},_getDragHorizontalDirection:function(){var B=this.positionAbs.left-this.lastPositionAbs.left;
return B!==0&&(B>0?"right":"left")
},refresh:function(B){this._refreshItems(B);
this._setHandleClassName();
this.refreshPositions();
return this
},_connectWith:function(){var B=this.options;
return B.connectWith.constructor===String?[B.connectWith]:B.connectWith
},_getItemsAsjQuery:function(B){var D,C,I,F,G=[],E=[],H=this._connectWith();
if(H&&B){for(D=H.length-1;
D>=0;
D--){I=A(H[D]);
for(C=I.length-1;
C>=0;
C--){F=A.data(I[C],this.widgetFullName);
if(F&&F!==this&&!F.options.disabled){E.push([A.isFunction(F.options.items)?F.options.items.call(F.element):A(F.options.items,F.element).not(".ui-sortable-helper").not(".ui-sortable-placeholder"),F])
}}}}E.push([A.isFunction(this.options.items)?this.options.items.call(this.element,null,{options:this.options,item:this.currentItem}):A(this.options.items,this.element).not(".ui-sortable-helper").not(".ui-sortable-placeholder"),this]);
function J(){G.push(this)
}for(D=E.length-1;
D>=0;
D--){E[D][0].each(J)
}return A(G)
},_removeCurrentsFromItems:function(){var B=this.currentItem.find(":data("+this.widgetName+"-item)");
this.items=A.grep(this.items,function(D){for(var C=0;
C<B.length;
C++){if(B[C]===D.item[0]){return false
}}return true
})
},_refreshItems:function(B){this.items=[];
this.containers=[this];
var F,D,K,G,J,C,M,L,H=this.items,E=[[A.isFunction(this.options.items)?this.options.items.call(this.element[0],B,{item:this.currentItem}):A(this.options.items,this.element),this]],I=this._connectWith();
if(I&&this.ready){for(F=I.length-1;
F>=0;
F--){K=A(I[F]);
for(D=K.length-1;
D>=0;
D--){G=A.data(K[D],this.widgetFullName);
if(G&&G!==this&&!G.options.disabled){E.push([A.isFunction(G.options.items)?G.options.items.call(G.element[0],B,{item:this.currentItem}):A(G.options.items,G.element),G]);
this.containers.push(G)
}}}}for(F=E.length-1;
F>=0;
F--){J=E[F][1];
C=E[F][0];
for(D=0,L=C.length;
D<L;
D++){M=A(C[D]);
M.data(this.widgetName+"-item",J);
H.push({item:M,instance:J,width:0,height:0,left:0,top:0})
}}},refreshPositions:function(B){if(this.offsetParent&&this.helper){this.offset.parent=this._getParentOffset()
}var D,E,C,F;
for(D=this.items.length-1;
D>=0;
D--){E=this.items[D];
if(E.instance!==this.currentContainer&&this.currentContainer&&E.item[0]!==this.currentItem[0]){continue
}C=this.options.toleranceElement?A(this.options.toleranceElement,E.item):E.item;
if(!B){E.width=C.outerWidth();
E.height=C.outerHeight()
}F=C.offset();
E.left=F.left;
E.top=F.top
}if(this.options.custom&&this.options.custom.refreshContainers){this.options.custom.refreshContainers.call(this)
}else{for(D=this.containers.length-1;
D>=0;
D--){F=this.containers[D].element.offset();
this.containers[D].containerCache.left=F.left;
this.containers[D].containerCache.top=F.top;
this.containers[D].containerCache.width=this.containers[D].element.outerWidth();
this.containers[D].containerCache.height=this.containers[D].element.outerHeight()
}}return this
},_createPlaceholder:function(C){C=C||this;
var B,D=C.options;
if(!D.placeholder||D.placeholder.constructor===String){B=D.placeholder;
D.placeholder={element:function(){var F=C.currentItem[0].nodeName.toLowerCase(),E=A("<"+F+">",C.document[0]).addClass(B||C.currentItem[0].className+" ui-sortable-placeholder").removeClass("ui-sortable-helper");
if(F==="tr"){C.currentItem.children().each(function(){A("<td>&#160;</td>",C.document[0]).attr("colspan",A(this).attr("colspan")||1).appendTo(E)
})
}else{if(F==="img"){E.attr("src",C.currentItem.attr("src"))
}}if(!B){E.css("visibility","hidden")
}return E
},update:function(E,F){if(B&&!D.forcePlaceholderSize){return 
}if(!F.height()){F.height(C.currentItem.innerHeight()-parseInt(C.currentItem.css("paddingTop")||0,10)-parseInt(C.currentItem.css("paddingBottom")||0,10))
}if(!F.width()){F.width(C.currentItem.innerWidth()-parseInt(C.currentItem.css("paddingLeft")||0,10)-parseInt(C.currentItem.css("paddingRight")||0,10))
}}}
}C.placeholder=A(D.placeholder.element.call(C.element,C.currentItem));
C.currentItem.after(C.placeholder);
D.placeholder.update(C,C.placeholder)
},_contactContainers:function(B){var G,E,K,H,I,M,N,F,J,D,C=null,L=null;
for(G=this.containers.length-1;
G>=0;
G--){if(A.contains(this.currentItem[0],this.containers[G].element[0])){continue
}if(this._intersectsWith(this.containers[G].containerCache)){if(C&&A.contains(this.containers[G].element[0],C.element[0])){continue
}C=this.containers[G];
L=G
}else{if(this.containers[G].containerCache.over){this.containers[G]._trigger("out",B,this._uiHash(this));
this.containers[G].containerCache.over=0
}}}if(!C){return 
}if(this.containers.length===1){if(!this.containers[L].containerCache.over){this.containers[L]._trigger("over",B,this._uiHash(this));
this.containers[L].containerCache.over=1
}}else{K=10000;
H=null;
J=C.floating||this._isFloating(this.currentItem);
I=J?"left":"top";
M=J?"width":"height";
D=J?"clientX":"clientY";
for(E=this.items.length-1;
E>=0;
E--){if(!A.contains(this.containers[L].element[0],this.items[E].item[0])){continue
}if(this.items[E].item[0]===this.currentItem[0]){continue
}N=this.items[E].item.offset()[I];
F=false;
if(B[D]-N>this.items[E][M]/2){F=true
}if(Math.abs(B[D]-N)<K){K=Math.abs(B[D]-N);
H=this.items[E];
this.direction=F?"up":"down"
}}if(!H&&!this.options.dropOnEmpty){return 
}if(this.currentContainer===this.containers[L]){if(!this.currentContainer.containerCache.over){this.containers[L]._trigger("over",B,this._uiHash());
this.currentContainer.containerCache.over=1
}return 
}H?this._rearrange(B,H,null,true):this._rearrange(B,null,this.containers[L].element,true);
this._trigger("change",B,this._uiHash());
this.containers[L]._trigger("change",B,this._uiHash(this));
this.currentContainer=this.containers[L];
this.options.placeholder.update(this.currentContainer,this.placeholder);
this.containers[L]._trigger("over",B,this._uiHash(this));
this.containers[L].containerCache.over=1
}},_createHelper:function(C){var D=this.options,B=A.isFunction(D.helper)?A(D.helper.apply(this.element[0],[C,this.currentItem])):(D.helper==="clone"?this.currentItem.clone():this.currentItem);
if(!B.parents("body").length){A(D.appendTo!=="parent"?D.appendTo:this.currentItem[0].parentNode)[0].appendChild(B[0])
}if(B[0]===this.currentItem[0]){this._storedCSS={width:this.currentItem[0].style.width,height:this.currentItem[0].style.height,position:this.currentItem.css("position"),top:this.currentItem.css("top"),left:this.currentItem.css("left")}
}if(!B[0].style.width||D.forceHelperSize){B.width(this.currentItem.width())
}if(!B[0].style.height||D.forceHelperSize){B.height(this.currentItem.height())
}return B
},_adjustOffsetFromHelper:function(B){if(typeof B==="string"){B=B.split(" ")
}if(A.isArray(B)){B={left:+B[0],top:+B[1]||0}
}if("left" in B){this.offset.click.left=B.left+this.margins.left
}if("right" in B){this.offset.click.left=this.helperProportions.width-B.right+this.margins.left
}if("top" in B){this.offset.click.top=B.top+this.margins.top
}if("bottom" in B){this.offset.click.top=this.helperProportions.height-B.bottom+this.margins.top
}},_getParentOffset:function(){this.offsetParent=this.helper.offsetParent();
var B=this.offsetParent.offset();
if(this.cssPosition==="absolute"&&this.scrollParent[0]!==document&&A.contains(this.scrollParent[0],this.offsetParent[0])){B.left+=this.scrollParent.scrollLeft();
B.top+=this.scrollParent.scrollTop()
}if(this.offsetParent[0]===document.body||(this.offsetParent[0].tagName&&this.offsetParent[0].tagName.toLowerCase()==="html"&&A.ui.ie)){B={top:0,left:0}
}return{top:B.top+(parseInt(this.offsetParent.css("borderTopWidth"),10)||0),left:B.left+(parseInt(this.offsetParent.css("borderLeftWidth"),10)||0)}
},_getRelativeOffset:function(){if(this.cssPosition==="relative"){var B=this.currentItem.position();
return{top:B.top-(parseInt(this.helper.css("top"),10)||0)+this.scrollParent.scrollTop(),left:B.left-(parseInt(this.helper.css("left"),10)||0)+this.scrollParent.scrollLeft()}
}else{return{top:0,left:0}
}},_cacheMargins:function(){this.margins={left:(parseInt(this.currentItem.css("marginLeft"),10)||0),top:(parseInt(this.currentItem.css("marginTop"),10)||0)}
},_cacheHelperProportions:function(){this.helperProportions={width:this.helper.outerWidth(),height:this.helper.outerHeight()}
},_setContainment:function(){var C,E,B,D=this.options;
if(D.containment==="parent"){D.containment=this.helper[0].parentNode
}if(D.containment==="document"||D.containment==="window"){this.containment=[0-this.offset.relative.left-this.offset.parent.left,0-this.offset.relative.top-this.offset.parent.top,A(D.containment==="document"?document:window).width()-this.helperProportions.width-this.margins.left,(A(D.containment==="document"?document:window).height()||document.body.parentNode.scrollHeight)-this.helperProportions.height-this.margins.top]
}if(!(/^(document|window|parent)$/).test(D.containment)){C=A(D.containment)[0];
E=A(D.containment).offset();
B=(A(C).css("overflow")!=="hidden");
this.containment=[E.left+(parseInt(A(C).css("borderLeftWidth"),10)||0)+(parseInt(A(C).css("paddingLeft"),10)||0)-this.margins.left,E.top+(parseInt(A(C).css("borderTopWidth"),10)||0)+(parseInt(A(C).css("paddingTop"),10)||0)-this.margins.top,E.left+(B?Math.max(C.scrollWidth,C.offsetWidth):C.offsetWidth)-(parseInt(A(C).css("borderLeftWidth"),10)||0)-(parseInt(A(C).css("paddingRight"),10)||0)-this.helperProportions.width-this.margins.left,E.top+(B?Math.max(C.scrollHeight,C.offsetHeight):C.offsetHeight)-(parseInt(A(C).css("borderTopWidth"),10)||0)-(parseInt(A(C).css("paddingBottom"),10)||0)-this.helperProportions.height-this.margins.top]
}},_convertPositionTo:function(D,F){if(!F){F=this.position
}var C=D==="absolute"?1:-1,B=this.cssPosition==="absolute"&&!(this.scrollParent[0]!==document&&A.contains(this.scrollParent[0],this.offsetParent[0]))?this.offsetParent:this.scrollParent,E=(/(html|body)/i).test(B[0].tagName);
return{top:(F.top+this.offset.relative.top*C+this.offset.parent.top*C-((this.cssPosition==="fixed"?-this.scrollParent.scrollTop():(E?0:B.scrollTop()))*C)),left:(F.left+this.offset.relative.left*C+this.offset.parent.left*C-((this.cssPosition==="fixed"?-this.scrollParent.scrollLeft():E?0:B.scrollLeft())*C))}
},_generatePosition:function(E){var G,F,H=this.options,D=E.pageX,C=E.pageY,B=this.cssPosition==="absolute"&&!(this.scrollParent[0]!==document&&A.contains(this.scrollParent[0],this.offsetParent[0]))?this.offsetParent:this.scrollParent,I=(/(html|body)/i).test(B[0].tagName);
if(this.cssPosition==="relative"&&!(this.scrollParent[0]!==document&&this.scrollParent[0]!==this.offsetParent[0])){this.offset.relative=this._getRelativeOffset()
}if(this.originalPosition){if(this.containment){if(E.pageX-this.offset.click.left<this.containment[0]){D=this.containment[0]+this.offset.click.left
}if(E.pageY-this.offset.click.top<this.containment[1]){C=this.containment[1]+this.offset.click.top
}if(E.pageX-this.offset.click.left>this.containment[2]){D=this.containment[2]+this.offset.click.left
}if(E.pageY-this.offset.click.top>this.containment[3]){C=this.containment[3]+this.offset.click.top
}}if(H.grid){G=this.originalPageY+Math.round((C-this.originalPageY)/H.grid[1])*H.grid[1];
C=this.containment?((G-this.offset.click.top>=this.containment[1]&&G-this.offset.click.top<=this.containment[3])?G:((G-this.offset.click.top>=this.containment[1])?G-H.grid[1]:G+H.grid[1])):G;
F=this.originalPageX+Math.round((D-this.originalPageX)/H.grid[0])*H.grid[0];
D=this.containment?((F-this.offset.click.left>=this.containment[0]&&F-this.offset.click.left<=this.containment[2])?F:((F-this.offset.click.left>=this.containment[0])?F-H.grid[0]:F+H.grid[0])):F
}}return{top:(C-this.offset.click.top-this.offset.relative.top-this.offset.parent.top+((this.cssPosition==="fixed"?-this.scrollParent.scrollTop():(I?0:B.scrollTop())))),left:(D-this.offset.click.left-this.offset.relative.left-this.offset.parent.left+((this.cssPosition==="fixed"?-this.scrollParent.scrollLeft():I?0:B.scrollLeft())))}
},_rearrange:function(F,E,C,D){C?C[0].appendChild(this.placeholder[0]):E.item[0].parentNode.insertBefore(this.placeholder[0],(this.direction==="down"?E.item[0]:E.item[0].nextSibling));
this.counter=this.counter?++this.counter:1;
var B=this.counter;
this._delay(function(){if(B===this.counter){this.refreshPositions(!D)
}})
},_clear:function(C,E){this.reverting=false;
var B,F=[];
if(!this._noFinalSort&&this.currentItem.parent().length){this.placeholder.before(this.currentItem)
}this._noFinalSort=null;
if(this.helper[0]===this.currentItem[0]){for(B in this._storedCSS){if(this._storedCSS[B]==="auto"||this._storedCSS[B]==="static"){this._storedCSS[B]=""
}}this.currentItem.css(this._storedCSS).removeClass("ui-sortable-helper")
}else{this.currentItem.show()
}if(this.fromOutside&&!E){F.push(function(G){this._trigger("receive",G,this._uiHash(this.fromOutside))
})
}if((this.fromOutside||this.domPosition.prev!==this.currentItem.prev().not(".ui-sortable-helper")[0]||this.domPosition.parent!==this.currentItem.parent()[0])&&!E){F.push(function(G){this._trigger("update",G,this._uiHash())
})
}if(this!==this.currentContainer){if(!E){F.push(function(G){this._trigger("remove",G,this._uiHash())
});
F.push((function(G){return function(H){G._trigger("receive",H,this._uiHash(this))
}
}).call(this,this.currentContainer));
F.push((function(G){return function(H){G._trigger("update",H,this._uiHash(this))
}
}).call(this,this.currentContainer))
}}function D(I,G,H){return function(J){H._trigger(I,J,G._uiHash(G))
}
}for(B=this.containers.length-1;
B>=0;
B--){if(!E){F.push(D("deactivate",this,this.containers[B]))
}if(this.containers[B].containerCache.over){F.push(D("out",this,this.containers[B]));
this.containers[B].containerCache.over=0
}}if(this.storedCursor){this.document.find("body").css("cursor",this.storedCursor);
this.storedStylesheet.remove()
}if(this._storedOpacity){this.helper.css("opacity",this._storedOpacity)
}if(this._storedZIndex){this.helper.css("zIndex",this._storedZIndex==="auto"?"":this._storedZIndex)
}this.dragging=false;
if(!E){this._trigger("beforeStop",C,this._uiHash())
}this.placeholder[0].parentNode.removeChild(this.placeholder[0]);
if(!this.cancelHelperRemoval){if(this.helper[0]!==this.currentItem[0]){this.helper.remove()
}this.helper=null
}if(!E){for(B=0;
B<F.length;
B++){F[B].call(this,C)
}this._trigger("stop",C,this._uiHash())
}this.fromOutside=false;
return !this.cancelHelperRemoval
},_trigger:function(){if(A.Widget.prototype._trigger.apply(this,arguments)===false){this.cancel()
}},_uiHash:function(B){var C=B||this;
return{helper:C.helper,placeholder:C.placeholder||A([]),position:C.position,originalPosition:C.originalPosition,offset:C.positionAbs,item:C.currentItem,sender:B?B.element:null}
}})
}));