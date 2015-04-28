/*
 * jQuery UI Draggable 1.11.2
 * http://jqueryui.com
 *
 * Copyright 2014 jQuery Foundation and other contributors
 * Released under the MIT license.
 * http://jquery.org/license
 *
 * http://api.jqueryui.com/draggable/
 */
(function(A){if(typeof define==="function"&&define.amd){define(["jquery","./core","./mouse","./widget"],A)
}else{A(jQuery)
}}(function(A){A.widget("ui.draggable",A.ui.mouse,{version:"1.11.2",widgetEventPrefix:"drag",options:{addClasses:true,appendTo:"parent",axis:false,connectToSortable:false,containment:false,cursor:"auto",cursorAt:false,grid:false,handle:false,helper:"original",iframeFix:false,opacity:false,refreshPositions:false,revert:false,revertDuration:500,scope:"default",scroll:true,scrollSensitivity:20,scrollSpeed:20,snap:false,snapMode:"both",snapTolerance:20,stack:false,zIndex:false,drag:null,start:null,stop:null},_create:function(){if(this.options.helper==="original"){this._setPositionRelative()
}if(this.options.addClasses){this.element.addClass("ui-draggable")
}if(this.options.disabled){this.element.addClass("ui-draggable-disabled")
}this._setHandleClassName();
this._mouseInit()
},_setOption:function(B,C){this._super(B,C);
if(B==="handle"){this._removeHandleClassName();
this._setHandleClassName()
}},_destroy:function(){if((this.helper||this.element).is(".ui-draggable-dragging")){this.destroyOnClear=true;
return 
}this.element.removeClass("ui-draggable ui-draggable-dragging ui-draggable-disabled");
this._removeHandleClassName();
this._mouseDestroy()
},_mouseCapture:function(B){var C=this.options;
this._blurActiveElement(B);
if(this.helper||C.disabled||A(B.target).closest(".ui-resizable-handle").length>0){return false
}this.handle=this._getHandle(B);
if(!this.handle){return false
}this._blockFrames(C.iframeFix===true?"iframe":C.iframeFix);
return true
},_blockFrames:function(B){this.iframeBlocks=this.document.find(B).map(function(){var C=A(this);
return A("<div>").css("position","absolute").appendTo(C.parent()).outerWidth(C.outerWidth()).outerHeight(C.outerHeight()).offset(C.offset())[0]
})
},_unblockFrames:function(){if(this.iframeBlocks){this.iframeBlocks.remove();
delete this.iframeBlocks
}},_blurActiveElement:function(D){var B=this.document[0];
if(!this.handleElement.is(D.target)){return 
}try{if(B.activeElement&&B.activeElement.nodeName.toLowerCase()!=="body"){A(B.activeElement).blur()
}}catch(C){}},_mouseStart:function(B){var C=this.options;
this.helper=this._createHelper(B);
this.helper.addClass("ui-draggable-dragging");
this._cacheHelperProportions();
if(A.ui.ddmanager){A.ui.ddmanager.current=this
}this._cacheMargins();
this.cssPosition=this.helper.css("position");
this.scrollParent=this.helper.scrollParent(true);
this.offsetParent=this.helper.offsetParent();
this.hasFixedAncestor=this.helper.parents().filter(function(){return A(this).css("position")==="fixed"
}).length>0;
this.positionAbs=this.element.offset();
this._refreshOffsets(B);
this.originalPosition=this.position=this._generatePosition(B,false);
this.originalPageX=B.pageX;
this.originalPageY=B.pageY;
(C.cursorAt&&this._adjustOffsetFromHelper(C.cursorAt));
this._setContainment();
if(this._trigger("start",B)===false){this._clear();
return false
}this._cacheHelperProportions();
if(A.ui.ddmanager&&!C.dropBehaviour){A.ui.ddmanager.prepareOffsets(this,B)
}this._normalizeRightBottom();
this._mouseDrag(B,true);
if(A.ui.ddmanager){A.ui.ddmanager.dragStart(this,B)
}return true
},_refreshOffsets:function(B){this.offset={top:this.positionAbs.top-this.margins.top,left:this.positionAbs.left-this.margins.left,scroll:false,parent:this._getParentOffset(),relative:this._getRelativeOffset()};
this.offset.click={left:B.pageX-this.offset.left,top:B.pageY-this.offset.top}
},_mouseDrag:function(B,D){if(this.hasFixedAncestor){this.offset.parent=this._getParentOffset()
}this.position=this._generatePosition(B,true);
this.positionAbs=this._convertPositionTo("absolute");
if(!D){var C=this._uiHash();
if(this._trigger("drag",B,C)===false){this._mouseUp({});
return false
}this.position=C.position
}this.helper[0].style.left=this.position.left+"px";
this.helper[0].style.top=this.position.top+"px";
if(A.ui.ddmanager){A.ui.ddmanager.drag(this,B)
}return false
},_mouseStop:function(C){var B=this,D=false;
if(A.ui.ddmanager&&!this.options.dropBehaviour){D=A.ui.ddmanager.drop(this,C)
}if(this.dropped){D=this.dropped;
this.dropped=false
}if((this.options.revert==="invalid"&&!D)||(this.options.revert==="valid"&&D)||this.options.revert===true||(A.isFunction(this.options.revert)&&this.options.revert.call(this.element,D))){A(this.helper).animate(this.originalPosition,parseInt(this.options.revertDuration,10),function(){if(B._trigger("stop",C)!==false){B._clear()
}})
}else{if(this._trigger("stop",C)!==false){this._clear()
}}return false
},_mouseUp:function(B){this._unblockFrames();
if(A.ui.ddmanager){A.ui.ddmanager.dragStop(this,B)
}if(this.handleElement.is(B.target)){this.element.focus()
}return A.ui.mouse.prototype._mouseUp.call(this,B)
},cancel:function(){if(this.helper.is(".ui-draggable-dragging")){this._mouseUp({})
}else{this._clear()
}return this
},_getHandle:function(B){return this.options.handle?!!A(B.target).closest(this.element.find(this.options.handle)).length:true
},_setHandleClassName:function(){this.handleElement=this.options.handle?this.element.find(this.options.handle):this.element;
this.handleElement.addClass("ui-draggable-handle")
},_removeHandleClassName:function(){this.handleElement.removeClass("ui-draggable-handle")
},_createHelper:function(C){var E=this.options,D=A.isFunction(E.helper),B=D?A(E.helper.apply(this.element[0],[C])):(E.helper==="clone"?this.element.clone().removeAttr("id"):this.element);
if(!B.parents("body").length){B.appendTo((E.appendTo==="parent"?this.element[0].parentNode:E.appendTo))
}if(D&&B[0]===this.element[0]){this._setPositionRelative()
}if(B[0]!==this.element[0]&&!(/(fixed|absolute)/).test(B.css("position"))){B.css("position","absolute")
}return B
},_setPositionRelative:function(){if(!(/^(?:r|a|f)/).test(this.element.css("position"))){this.element[0].style.position="relative"
}},_adjustOffsetFromHelper:function(B){if(typeof B==="string"){B=B.split(" ")
}if(A.isArray(B)){B={left:+B[0],top:+B[1]||0}
}if("left" in B){this.offset.click.left=B.left+this.margins.left
}if("right" in B){this.offset.click.left=this.helperProportions.width-B.right+this.margins.left
}if("top" in B){this.offset.click.top=B.top+this.margins.top
}if("bottom" in B){this.offset.click.top=this.helperProportions.height-B.bottom+this.margins.top
}},_isRootNode:function(B){return(/(html|body)/i).test(B.tagName)||B===this.document[0]
},_getParentOffset:function(){var C=this.offsetParent.offset(),B=this.document[0];
if(this.cssPosition==="absolute"&&this.scrollParent[0]!==B&&A.contains(this.scrollParent[0],this.offsetParent[0])){C.left+=this.scrollParent.scrollLeft();
C.top+=this.scrollParent.scrollTop()
}if(this._isRootNode(this.offsetParent[0])){C={top:0,left:0}
}return{top:C.top+(parseInt(this.offsetParent.css("borderTopWidth"),10)||0),left:C.left+(parseInt(this.offsetParent.css("borderLeftWidth"),10)||0)}
},_getRelativeOffset:function(){if(this.cssPosition!=="relative"){return{top:0,left:0}
}var B=this.element.position(),C=this._isRootNode(this.scrollParent[0]);
return{top:B.top-(parseInt(this.helper.css("top"),10)||0)+(!C?this.scrollParent.scrollTop():0),left:B.left-(parseInt(this.helper.css("left"),10)||0)+(!C?this.scrollParent.scrollLeft():0)}
},_cacheMargins:function(){this.margins={left:(parseInt(this.element.css("marginLeft"),10)||0),top:(parseInt(this.element.css("marginTop"),10)||0),right:(parseInt(this.element.css("marginRight"),10)||0),bottom:(parseInt(this.element.css("marginBottom"),10)||0)}
},_cacheHelperProportions:function(){this.helperProportions={width:this.helper.outerWidth(),height:this.helper.outerHeight()}
},_setContainment:function(){var C,F,D,E=this.options,B=this.document[0];
this.relativeContainer=null;
if(!E.containment){this.containment=null;
return 
}if(E.containment==="window"){this.containment=[A(window).scrollLeft()-this.offset.relative.left-this.offset.parent.left,A(window).scrollTop()-this.offset.relative.top-this.offset.parent.top,A(window).scrollLeft()+A(window).width()-this.helperProportions.width-this.margins.left,A(window).scrollTop()+(A(window).height()||B.body.parentNode.scrollHeight)-this.helperProportions.height-this.margins.top];
return 
}if(E.containment==="document"){this.containment=[0,0,A(B).width()-this.helperProportions.width-this.margins.left,(A(B).height()||B.body.parentNode.scrollHeight)-this.helperProportions.height-this.margins.top];
return 
}if(E.containment.constructor===Array){this.containment=E.containment;
return 
}if(E.containment==="parent"){E.containment=this.helper[0].parentNode
}F=A(E.containment);
D=F[0];
if(!D){return 
}C=/(scroll|auto)/.test(F.css("overflow"));
this.containment=[(parseInt(F.css("borderLeftWidth"),10)||0)+(parseInt(F.css("paddingLeft"),10)||0),(parseInt(F.css("borderTopWidth"),10)||0)+(parseInt(F.css("paddingTop"),10)||0),(C?Math.max(D.scrollWidth,D.offsetWidth):D.offsetWidth)-(parseInt(F.css("borderRightWidth"),10)||0)-(parseInt(F.css("paddingRight"),10)||0)-this.helperProportions.width-this.margins.left-this.margins.right,(C?Math.max(D.scrollHeight,D.offsetHeight):D.offsetHeight)-(parseInt(F.css("borderBottomWidth"),10)||0)-(parseInt(F.css("paddingBottom"),10)||0)-this.helperProportions.height-this.margins.top-this.margins.bottom];
this.relativeContainer=F
},_convertPositionTo:function(C,E){if(!E){E=this.position
}var B=C==="absolute"?1:-1,D=this._isRootNode(this.scrollParent[0]);
return{top:(E.top+this.offset.relative.top*B+this.offset.parent.top*B-((this.cssPosition==="fixed"?-this.offset.scroll.top:(D?0:this.offset.scroll.top))*B)),left:(E.left+this.offset.relative.left*B+this.offset.parent.left*B-((this.cssPosition==="fixed"?-this.offset.scroll.left:(D?0:this.offset.scroll.left))*B))}
},_generatePosition:function(C,I){var B,J,K,E,D=this.options,H=this._isRootNode(this.scrollParent[0]),G=C.pageX,F=C.pageY;
if(!H||!this.offset.scroll){this.offset.scroll={top:this.scrollParent.scrollTop(),left:this.scrollParent.scrollLeft()}
}if(I){if(this.containment){if(this.relativeContainer){J=this.relativeContainer.offset();
B=[this.containment[0]+J.left,this.containment[1]+J.top,this.containment[2]+J.left,this.containment[3]+J.top]
}else{B=this.containment
}if(C.pageX-this.offset.click.left<B[0]){G=B[0]+this.offset.click.left
}if(C.pageY-this.offset.click.top<B[1]){F=B[1]+this.offset.click.top
}if(C.pageX-this.offset.click.left>B[2]){G=B[2]+this.offset.click.left
}if(C.pageY-this.offset.click.top>B[3]){F=B[3]+this.offset.click.top
}}if(D.grid){K=D.grid[1]?this.originalPageY+Math.round((F-this.originalPageY)/D.grid[1])*D.grid[1]:this.originalPageY;
F=B?((K-this.offset.click.top>=B[1]||K-this.offset.click.top>B[3])?K:((K-this.offset.click.top>=B[1])?K-D.grid[1]:K+D.grid[1])):K;
E=D.grid[0]?this.originalPageX+Math.round((G-this.originalPageX)/D.grid[0])*D.grid[0]:this.originalPageX;
G=B?((E-this.offset.click.left>=B[0]||E-this.offset.click.left>B[2])?E:((E-this.offset.click.left>=B[0])?E-D.grid[0]:E+D.grid[0])):E
}if(D.axis==="y"){G=this.originalPageX
}if(D.axis==="x"){F=this.originalPageY
}}return{top:(F-this.offset.click.top-this.offset.relative.top-this.offset.parent.top+(this.cssPosition==="fixed"?-this.offset.scroll.top:(H?0:this.offset.scroll.top))),left:(G-this.offset.click.left-this.offset.relative.left-this.offset.parent.left+(this.cssPosition==="fixed"?-this.offset.scroll.left:(H?0:this.offset.scroll.left)))}
},_clear:function(){this.helper.removeClass("ui-draggable-dragging");
if(this.helper[0]!==this.element[0]&&!this.cancelHelperRemoval){this.helper.remove()
}this.helper=null;
this.cancelHelperRemoval=false;
if(this.destroyOnClear){this.destroy()
}},_normalizeRightBottom:function(){if(this.options.axis!=="y"&&this.helper.css("right")!=="auto"){this.helper.width(this.helper.width());
this.helper.css("right","auto")
}if(this.options.axis!=="x"&&this.helper.css("bottom")!=="auto"){this.helper.height(this.helper.height());
this.helper.css("bottom","auto")
}},_trigger:function(B,C,D){D=D||this._uiHash();
A.ui.plugin.call(this,B,[C,D,this],true);
if(/^(drag|start|stop)/.test(B)){this.positionAbs=this._convertPositionTo("absolute");
D.offset=this.positionAbs
}return A.Widget.prototype._trigger.call(this,B,C,D)
},plugins:{},_uiHash:function(){return{helper:this.helper,position:this.position,originalPosition:this.originalPosition,offset:this.positionAbs}
}});
A.ui.plugin.add("draggable","connectToSortable",{start:function(D,E,B){var C=A.extend({},E,{item:B.element});
B.sortables=[];
A(B.options.connectToSortable).each(function(){var F=A(this).sortable("instance");
if(F&&!F.options.disabled){B.sortables.push(F);
F.refreshPositions();
F._trigger("activate",D,C)
}})
},stop:function(D,E,B){var C=A.extend({},E,{item:B.element});
B.cancelHelperRemoval=false;
A.each(B.sortables,function(){var F=this;
if(F.isOver){F.isOver=0;
B.cancelHelperRemoval=true;
F.cancelHelperRemoval=false;
F._storedCSS={position:F.placeholder.css("position"),top:F.placeholder.css("top"),left:F.placeholder.css("left")};
F._mouseStop(D);
F.options.helper=F.options._helper
}else{F.cancelHelperRemoval=true;
F._trigger("deactivate",D,C)
}})
},drag:function(C,D,B){A.each(B.sortables,function(){var E=false,F=this;
F.positionAbs=B.positionAbs;
F.helperProportions=B.helperProportions;
F.offset.click=B.offset.click;
if(F._intersectsWith(F.containerCache)){E=true;
A.each(B.sortables,function(){this.positionAbs=B.positionAbs;
this.helperProportions=B.helperProportions;
this.offset.click=B.offset.click;
if(this!==F&&this._intersectsWith(this.containerCache)&&A.contains(F.element[0],this.element[0])){E=false
}return E
})
}if(E){if(!F.isOver){F.isOver=1;
F.currentItem=D.helper.appendTo(F.element).data("ui-sortable-item",true);
F.options._helper=F.options.helper;
F.options.helper=function(){return D.helper[0]
};
C.target=F.currentItem[0];
F._mouseCapture(C,true);
F._mouseStart(C,true,true);
F.offset.click.top=B.offset.click.top;
F.offset.click.left=B.offset.click.left;
F.offset.parent.left-=B.offset.parent.left-F.offset.parent.left;
F.offset.parent.top-=B.offset.parent.top-F.offset.parent.top;
B._trigger("toSortable",C);
B.dropped=F.element;
A.each(B.sortables,function(){this.refreshPositions()
});
B.currentItem=B.element;
F.fromOutside=B
}if(F.currentItem){F._mouseDrag(C);
D.position=F.position
}}else{if(F.isOver){F.isOver=0;
F.cancelHelperRemoval=true;
F.options._revert=F.options.revert;
F.options.revert=false;
F._trigger("out",C,F._uiHash(F));
F._mouseStop(C,true);
F.options.revert=F.options._revert;
F.options.helper=F.options._helper;
if(F.placeholder){F.placeholder.remove()
}B._refreshOffsets(C);
D.position=B._generatePosition(C,true);
B._trigger("fromSortable",C);
B.dropped=false;
A.each(B.sortables,function(){this.refreshPositions()
})
}}})
}});
A.ui.plugin.add("draggable","cursor",{start:function(D,E,B){var C=A("body"),F=B.options;
if(C.css("cursor")){F._cursor=C.css("cursor")
}C.css("cursor",F.cursor)
},stop:function(C,D,B){var E=B.options;
if(E._cursor){A("body").css("cursor",E._cursor)
}}});
A.ui.plugin.add("draggable","opacity",{start:function(D,E,B){var C=A(E.helper),F=B.options;
if(C.css("opacity")){F._opacity=C.css("opacity")
}C.css("opacity",F.opacity)
},stop:function(C,D,B){var E=B.options;
if(E._opacity){A(D.helper).css("opacity",E._opacity)
}}});
A.ui.plugin.add("draggable","scroll",{start:function(C,D,B){if(!B.scrollParentNotHidden){B.scrollParentNotHidden=B.helper.scrollParent(false)
}if(B.scrollParentNotHidden[0]!==B.document[0]&&B.scrollParentNotHidden[0].tagName!=="HTML"){B.overflowOffset=B.scrollParentNotHidden.offset()
}},drag:function(E,F,D){var G=D.options,C=false,H=D.scrollParentNotHidden[0],B=D.document[0];
if(H!==B&&H.tagName!=="HTML"){if(!G.axis||G.axis!=="x"){if((D.overflowOffset.top+H.offsetHeight)-E.pageY<G.scrollSensitivity){H.scrollTop=C=H.scrollTop+G.scrollSpeed
}else{if(E.pageY-D.overflowOffset.top<G.scrollSensitivity){H.scrollTop=C=H.scrollTop-G.scrollSpeed
}}}if(!G.axis||G.axis!=="y"){if((D.overflowOffset.left+H.offsetWidth)-E.pageX<G.scrollSensitivity){H.scrollLeft=C=H.scrollLeft+G.scrollSpeed
}else{if(E.pageX-D.overflowOffset.left<G.scrollSensitivity){H.scrollLeft=C=H.scrollLeft-G.scrollSpeed
}}}}else{if(!G.axis||G.axis!=="x"){if(E.pageY-A(B).scrollTop()<G.scrollSensitivity){C=A(B).scrollTop(A(B).scrollTop()-G.scrollSpeed)
}else{if(A(window).height()-(E.pageY-A(B).scrollTop())<G.scrollSensitivity){C=A(B).scrollTop(A(B).scrollTop()+G.scrollSpeed)
}}}if(!G.axis||G.axis!=="y"){if(E.pageX-A(B).scrollLeft()<G.scrollSensitivity){C=A(B).scrollLeft(A(B).scrollLeft()-G.scrollSpeed)
}else{if(A(window).width()-(E.pageX-A(B).scrollLeft())<G.scrollSensitivity){C=A(B).scrollLeft(A(B).scrollLeft()+G.scrollSpeed)
}}}}if(C!==false&&A.ui.ddmanager&&!G.dropBehaviour){A.ui.ddmanager.prepareOffsets(D,E)
}}});
A.ui.plugin.add("draggable","snap",{start:function(C,D,B){var E=B.options;
B.snapElements=[];
A(E.snap.constructor!==String?(E.snap.items||":data(ui-draggable)"):E.snap).each(function(){var G=A(this),F=G.offset();
if(this!==B.element[0]){B.snapElements.push({item:this,width:G.outerWidth(),height:G.outerHeight(),top:F.top,left:F.left})
}})
},drag:function(N,K,E){var B,S,G,H,M,J,I,T,O,F,L=E.options,R=L.snapTolerance,Q=K.offset.left,P=Q+E.helperProportions.width,D=K.offset.top,C=D+E.helperProportions.height;
for(O=E.snapElements.length-1;
O>=0;
O--){M=E.snapElements[O].left-E.margins.left;
J=M+E.snapElements[O].width;
I=E.snapElements[O].top-E.margins.top;
T=I+E.snapElements[O].height;
if(P<M-R||Q>J+R||C<I-R||D>T+R||!A.contains(E.snapElements[O].item.ownerDocument,E.snapElements[O].item)){if(E.snapElements[O].snapping){(E.options.snap.release&&E.options.snap.release.call(E.element,N,A.extend(E._uiHash(),{snapItem:E.snapElements[O].item})))
}E.snapElements[O].snapping=false;
continue
}if(L.snapMode!=="inner"){B=Math.abs(I-C)<=R;
S=Math.abs(T-D)<=R;
G=Math.abs(M-P)<=R;
H=Math.abs(J-Q)<=R;
if(B){K.position.top=E._convertPositionTo("relative",{top:I-E.helperProportions.height,left:0}).top
}if(S){K.position.top=E._convertPositionTo("relative",{top:T,left:0}).top
}if(G){K.position.left=E._convertPositionTo("relative",{top:0,left:M-E.helperProportions.width}).left
}if(H){K.position.left=E._convertPositionTo("relative",{top:0,left:J}).left
}}F=(B||S||G||H);
if(L.snapMode!=="outer"){B=Math.abs(I-D)<=R;
S=Math.abs(T-C)<=R;
G=Math.abs(M-Q)<=R;
H=Math.abs(J-P)<=R;
if(B){K.position.top=E._convertPositionTo("relative",{top:I,left:0}).top
}if(S){K.position.top=E._convertPositionTo("relative",{top:T-E.helperProportions.height,left:0}).top
}if(G){K.position.left=E._convertPositionTo("relative",{top:0,left:M}).left
}if(H){K.position.left=E._convertPositionTo("relative",{top:0,left:J-E.helperProportions.width}).left
}}if(!E.snapElements[O].snapping&&(B||S||G||H||F)){(E.options.snap.snap&&E.options.snap.snap.call(E.element,N,A.extend(E._uiHash(),{snapItem:E.snapElements[O].item})))
}E.snapElements[O].snapping=(B||S||G||H||F)
}}});
A.ui.plugin.add("draggable","stack",{start:function(D,E,B){var C,G=B.options,F=A.makeArray(A(G.stack)).sort(function(I,H){return(parseInt(A(I).css("zIndex"),10)||0)-(parseInt(A(H).css("zIndex"),10)||0)
});
if(!F.length){return 
}C=parseInt(A(F[0]).css("zIndex"),10)||0;
A(F).each(function(H){A(this).css("zIndex",C+H)
});
this.css("zIndex",(C+F.length))
}});
A.ui.plugin.add("draggable","zIndex",{start:function(D,E,B){var C=A(E.helper),F=B.options;
if(C.css("zIndex")){F._zIndex=C.css("zIndex")
}C.css("zIndex",F.zIndex)
},stop:function(C,D,B){var E=B.options;
if(E._zIndex){A(D.helper).css("zIndex",E._zIndex)
}}});
return A.ui.draggable
}));