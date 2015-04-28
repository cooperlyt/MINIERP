/*
 * jQuery UI Resizable 1.11.2
 * http://jqueryui.com
 *
 * Copyright 2014 jQuery Foundation and other contributors
 * Released under the MIT license.
 * http://jquery.org/license
 *
 * http://api.jqueryui.com/resizable/
 */
(function(A){if(typeof define==="function"&&define.amd){define(["jquery","./core","./mouse","./widget"],A)
}else{A(jQuery)
}}(function(A){A.widget("ui.resizable",A.ui.mouse,{version:"1.11.2",widgetEventPrefix:"resize",options:{alsoResize:false,animate:false,animateDuration:"slow",animateEasing:"swing",aspectRatio:false,autoHide:false,containment:false,ghost:false,grid:false,handles:"e,s,se",helper:false,maxHeight:null,maxWidth:null,minHeight:10,minWidth:10,zIndex:90,resize:null,start:null,stop:null},_num:function(B){return parseInt(B,10)||0
},_isNumber:function(B){return !isNaN(parseInt(B,10))
},_hasScroll:function(E,C){if(A(E).css("overflow")==="hidden"){return false
}var B=(C&&C==="left")?"scrollLeft":"scrollTop",D=false;
if(E[B]>0){return true
}E[B]=1;
D=(E[B]>0);
E[B]=0;
return D
},_create:function(){var H,C,F,D,B,E=this,G=this.options;
this.element.addClass("ui-resizable");
A.extend(this,{_aspectRatio:!!(G.aspectRatio),aspectRatio:G.aspectRatio,originalElement:this.element,_proportionallyResizeElements:[],_helper:G.helper||G.ghost||G.animate?G.helper||"ui-resizable-helper":null});
if(this.element[0].nodeName.match(/canvas|textarea|input|select|button|img/i)){this.element.wrap(A("<div class='ui-wrapper' style='overflow: hidden;'></div>").css({position:this.element.css("position"),width:this.element.outerWidth(),height:this.element.outerHeight(),top:this.element.css("top"),left:this.element.css("left")}));
this.element=this.element.parent().data("ui-resizable",this.element.resizable("instance"));
this.elementIsWrapper=true;
this.element.css({marginLeft:this.originalElement.css("marginLeft"),marginTop:this.originalElement.css("marginTop"),marginRight:this.originalElement.css("marginRight"),marginBottom:this.originalElement.css("marginBottom")});
this.originalElement.css({marginLeft:0,marginTop:0,marginRight:0,marginBottom:0});
this.originalResizeStyle=this.originalElement.css("resize");
this.originalElement.css("resize","none");
this._proportionallyResizeElements.push(this.originalElement.css({position:"static",zoom:1,display:"block"}));
this.originalElement.css({margin:this.originalElement.css("margin")});
this._proportionallyResize()
}this.handles=G.handles||(!A(".ui-resizable-handle",this.element).length?"e,s,se":{n:".ui-resizable-n",e:".ui-resizable-e",s:".ui-resizable-s",w:".ui-resizable-w",se:".ui-resizable-se",sw:".ui-resizable-sw",ne:".ui-resizable-ne",nw:".ui-resizable-nw"});
if(this.handles.constructor===String){if(this.handles==="all"){this.handles="n,e,s,w,se,sw,ne,nw"
}H=this.handles.split(",");
this.handles={};
for(C=0;
C<H.length;
C++){F=A.trim(H[C]);
B="ui-resizable-"+F;
D=A("<div class='ui-resizable-handle "+B+"'></div>");
D.css({zIndex:G.zIndex});
if("se"===F){D.addClass("ui-icon ui-icon-gripsmall-diagonal-se")
}this.handles[F]=".ui-resizable-"+F;
this.element.append(D)
}}this._renderAxis=function(M){var J,K,I,L;
M=M||this.element;
for(J in this.handles){if(this.handles[J].constructor===String){this.handles[J]=this.element.children(this.handles[J]).first().show()
}if(this.elementIsWrapper&&this.originalElement[0].nodeName.match(/textarea|input|select|button/i)){K=A(this.handles[J],this.element);
L=/sw|ne|nw|se|n|s/.test(J)?K.outerHeight():K.outerWidth();
I=["padding",/ne|nw|n/.test(J)?"Top":/se|sw|s/.test(J)?"Bottom":/^e$/.test(J)?"Right":"Left"].join("");
M.css(I,L);
this._proportionallyResize()
}if(!A(this.handles[J]).length){continue
}}};
this._renderAxis(this.element);
this._handles=A(".ui-resizable-handle",this.element).disableSelection();
this._handles.mouseover(function(){if(!E.resizing){if(this.className){D=this.className.match(/ui-resizable-(se|sw|ne|nw|n|e|s|w)/i)
}E.axis=D&&D[1]?D[1]:"se"
}});
if(G.autoHide){this._handles.hide();
A(this.element).addClass("ui-resizable-autohide").mouseenter(function(){if(G.disabled){return 
}A(this).removeClass("ui-resizable-autohide");
E._handles.show()
}).mouseleave(function(){if(G.disabled){return 
}if(!E.resizing){A(this).addClass("ui-resizable-autohide");
E._handles.hide()
}})
}this._mouseInit()
},_destroy:function(){this._mouseDestroy();
var C,B=function(D){A(D).removeClass("ui-resizable ui-resizable-disabled ui-resizable-resizing").removeData("resizable").removeData("ui-resizable").unbind(".resizable").find(".ui-resizable-handle").remove()
};
if(this.elementIsWrapper){B(this.element);
C=this.element;
this.originalElement.css({position:C.css("position"),width:C.outerWidth(),height:C.outerHeight(),top:C.css("top"),left:C.css("left")}).insertAfter(C);
C.remove()
}this.originalElement.css("resize",this.originalResizeStyle);
B(this.originalElement);
return this
},_mouseCapture:function(D){var C,E,B=false;
for(C in this.handles){E=A(this.handles[C])[0];
if(E===D.target||A.contains(E,D.target)){B=true
}}return !this.options.disabled&&B
},_mouseStart:function(C){var G,D,F,E=this.options,B=this.element;
this.resizing=true;
this._renderProxy();
G=this._num(this.helper.css("left"));
D=this._num(this.helper.css("top"));
if(E.containment){G+=A(E.containment).scrollLeft()||0;
D+=A(E.containment).scrollTop()||0
}this.offset=this.helper.offset();
this.position={left:G,top:D};
this.size=this._helper?{width:this.helper.width(),height:this.helper.height()}:{width:B.width(),height:B.height()};
this.originalSize=this._helper?{width:B.outerWidth(),height:B.outerHeight()}:{width:B.width(),height:B.height()};
this.sizeDiff={width:B.outerWidth()-B.width(),height:B.outerHeight()-B.height()};
this.originalPosition={left:G,top:D};
this.originalMousePosition={left:C.pageX,top:C.pageY};
this.aspectRatio=(typeof E.aspectRatio==="number")?E.aspectRatio:((this.originalSize.width/this.originalSize.height)||1);
F=A(".ui-resizable-"+this.axis).css("cursor");
A("body").css("cursor",F==="auto"?this.axis+"-resize":F);
B.addClass("ui-resizable-resizing");
this._propagate("start",C);
return true
},_mouseDrag:function(G){var H,F,I=this.originalMousePosition,C=this.axis,D=(G.pageX-I.left)||0,B=(G.pageY-I.top)||0,E=this._change[C];
this._updatePrevProperties();
if(!E){return false
}H=E.apply(this,[G,D,B]);
this._updateVirtualBoundaries(G.shiftKey);
if(this._aspectRatio||G.shiftKey){H=this._updateRatio(H,G)
}H=this._respectSize(H,G);
this._updateCache(H);
this._propagate("resize",G);
F=this._applyChanges();
if(!this._helper&&this._proportionallyResizeElements.length){this._proportionallyResize()
}if(!A.isEmptyObject(F)){this._updatePrevProperties();
this._trigger("resize",G,this.ui());
this._applyChanges()
}return false
},_mouseStop:function(E){this.resizing=false;
var D,B,C,H,K,G,J,F=this.options,I=this;
if(this._helper){D=this._proportionallyResizeElements;
B=D.length&&(/textarea/i).test(D[0].nodeName);
C=B&&this._hasScroll(D[0],"left")?0:I.sizeDiff.height;
H=B?0:I.sizeDiff.width;
K={width:(I.helper.width()-H),height:(I.helper.height()-C)};
G=(parseInt(I.element.css("left"),10)+(I.position.left-I.originalPosition.left))||null;
J=(parseInt(I.element.css("top"),10)+(I.position.top-I.originalPosition.top))||null;
if(!F.animate){this.element.css(A.extend(K,{top:J,left:G}))
}I.helper.height(I.size.height);
I.helper.width(I.size.width);
if(this._helper&&!F.animate){this._proportionallyResize()
}}A("body").css("cursor","auto");
this.element.removeClass("ui-resizable-resizing");
this._propagate("stop",E);
if(this._helper){this.helper.remove()
}return false
},_updatePrevProperties:function(){this.prevPosition={top:this.position.top,left:this.position.left};
this.prevSize={width:this.size.width,height:this.size.height}
},_applyChanges:function(){var B={};
if(this.position.top!==this.prevPosition.top){B.top=this.position.top+"px"
}if(this.position.left!==this.prevPosition.left){B.left=this.position.left+"px"
}if(this.size.width!==this.prevSize.width){B.width=this.size.width+"px"
}if(this.size.height!==this.prevSize.height){B.height=this.size.height+"px"
}this.helper.css(B);
return B
},_updateVirtualBoundaries:function(D){var F,E,C,H,B,G=this.options;
B={minWidth:this._isNumber(G.minWidth)?G.minWidth:0,maxWidth:this._isNumber(G.maxWidth)?G.maxWidth:Infinity,minHeight:this._isNumber(G.minHeight)?G.minHeight:0,maxHeight:this._isNumber(G.maxHeight)?G.maxHeight:Infinity};
if(this._aspectRatio||D){F=B.minHeight*this.aspectRatio;
C=B.minWidth/this.aspectRatio;
E=B.maxHeight*this.aspectRatio;
H=B.maxWidth/this.aspectRatio;
if(F>B.minWidth){B.minWidth=F
}if(C>B.minHeight){B.minHeight=C
}if(E<B.maxWidth){B.maxWidth=E
}if(H<B.maxHeight){B.maxHeight=H
}}this._vBoundaries=B
},_updateCache:function(B){this.offset=this.helper.offset();
if(this._isNumber(B.left)){this.position.left=B.left
}if(this._isNumber(B.top)){this.position.top=B.top
}if(this._isNumber(B.height)){this.size.height=B.height
}if(this._isNumber(B.width)){this.size.width=B.width
}},_updateRatio:function(D){var E=this.position,C=this.size,B=this.axis;
if(this._isNumber(D.height)){D.width=(D.height*this.aspectRatio)
}else{if(this._isNumber(D.width)){D.height=(D.width/this.aspectRatio)
}}if(B==="sw"){D.left=E.left+(C.width-D.width);
D.top=null
}if(B==="nw"){D.top=E.top+(C.height-D.height);
D.left=E.left+(C.width-D.width)
}return D
},_respectSize:function(G){var D=this._vBoundaries,J=this.axis,L=this._isNumber(G.width)&&D.maxWidth&&(D.maxWidth<G.width),H=this._isNumber(G.height)&&D.maxHeight&&(D.maxHeight<G.height),E=this._isNumber(G.width)&&D.minWidth&&(D.minWidth>G.width),K=this._isNumber(G.height)&&D.minHeight&&(D.minHeight>G.height),C=this.originalPosition.left+this.originalSize.width,I=this.position.top+this.size.height,F=/sw|nw|w/.test(J),B=/nw|ne|n/.test(J);
if(E){G.width=D.minWidth
}if(K){G.height=D.minHeight
}if(L){G.width=D.maxWidth
}if(H){G.height=D.maxHeight
}if(E&&F){G.left=C-D.minWidth
}if(L&&F){G.left=C-D.maxWidth
}if(K&&B){G.top=I-D.minHeight
}if(H&&B){G.top=I-D.maxHeight
}if(!G.width&&!G.height&&!G.left&&G.top){G.top=null
}else{if(!G.width&&!G.height&&!G.top&&G.left){G.left=null
}}return G
},_getPaddingPlusBorderDimensions:function(D){var C=0,E=[],F=[D.css("borderTopWidth"),D.css("borderRightWidth"),D.css("borderBottomWidth"),D.css("borderLeftWidth")],B=[D.css("paddingTop"),D.css("paddingRight"),D.css("paddingBottom"),D.css("paddingLeft")];
for(;
C<4;
C++){E[C]=(parseInt(F[C],10)||0);
E[C]+=(parseInt(B[C],10)||0)
}return{height:E[0]+E[2],width:E[1]+E[3]}
},_proportionallyResize:function(){if(!this._proportionallyResizeElements.length){return 
}var D,C=0,B=this.helper||this.element;
for(;
C<this._proportionallyResizeElements.length;
C++){D=this._proportionallyResizeElements[C];
if(!this.outerDimensions){this.outerDimensions=this._getPaddingPlusBorderDimensions(D)
}D.css({height:(B.height()-this.outerDimensions.height)||0,width:(B.width()-this.outerDimensions.width)||0})
}},_renderProxy:function(){var B=this.element,C=this.options;
this.elementOffset=B.offset();
if(this._helper){this.helper=this.helper||A("<div style='overflow:hidden;'></div>");
this.helper.addClass(this._helper).css({width:this.element.outerWidth()-1,height:this.element.outerHeight()-1,position:"absolute",left:this.elementOffset.left+"px",top:this.elementOffset.top+"px",zIndex:++C.zIndex});
this.helper.appendTo("body").disableSelection()
}else{this.helper=this.element
}},_change:{e:function(C,B){return{width:this.originalSize.width+B}
},w:function(D,B){var C=this.originalSize,E=this.originalPosition;
return{left:E.left+B,width:C.width-B}
},n:function(E,C,B){var D=this.originalSize,F=this.originalPosition;
return{top:F.top+B,height:D.height-B}
},s:function(D,C,B){return{height:this.originalSize.height+B}
},se:function(D,C,B){return A.extend(this._change.s.apply(this,arguments),this._change.e.apply(this,[D,C,B]))
},sw:function(D,C,B){return A.extend(this._change.s.apply(this,arguments),this._change.w.apply(this,[D,C,B]))
},ne:function(D,C,B){return A.extend(this._change.n.apply(this,arguments),this._change.e.apply(this,[D,C,B]))
},nw:function(D,C,B){return A.extend(this._change.n.apply(this,arguments),this._change.w.apply(this,[D,C,B]))
}},_propagate:function(C,B){A.ui.plugin.call(this,C,[B,this.ui()]);
(C!=="resize"&&this._trigger(C,B,this.ui()))
},plugins:{},ui:function(){return{originalElement:this.originalElement,element:this.element,helper:this.helper,position:this.position,size:this.size,originalSize:this.originalSize,originalPosition:this.originalPosition}
}});
A.ui.plugin.add("resizable","animate",{stop:function(E){var J=A(this).resizable("instance"),G=J.options,D=J._proportionallyResizeElements,B=D.length&&(/textarea/i).test(D[0].nodeName),C=B&&J._hasScroll(D[0],"left")?0:J.sizeDiff.height,I=B?0:J.sizeDiff.width,F={width:(J.size.width-I),height:(J.size.height-C)},H=(parseInt(J.element.css("left"),10)+(J.position.left-J.originalPosition.left))||null,K=(parseInt(J.element.css("top"),10)+(J.position.top-J.originalPosition.top))||null;
J.element.animate(A.extend(F,K&&H?{top:K,left:H}:{}),{duration:G.animateDuration,easing:G.animateEasing,step:function(){var L={width:parseInt(J.element.css("width"),10),height:parseInt(J.element.css("height"),10),top:parseInt(J.element.css("top"),10),left:parseInt(J.element.css("left"),10)};
if(D&&D.length){A(D[0]).css({width:L.width,height:L.height})
}J._updateCache(L);
J._propagate("resize",E)
}})
}});
A.ui.plugin.add("resizable","containment",{start:function(){var J,D,L,B,I,E,M,K=A(this).resizable("instance"),H=K.options,G=K.element,C=H.containment,F=(C instanceof A)?C.get(0):(/parent/.test(C))?G.parent().get(0):C;
if(!F){return 
}K.containerElement=A(F);
if(/document/.test(C)||C===document){K.containerOffset={left:0,top:0};
K.containerPosition={left:0,top:0};
K.parentData={element:A(document),left:0,top:0,width:A(document).width(),height:A(document).height()||document.body.parentNode.scrollHeight}
}else{J=A(F);
D=[];
A(["Top","Right","Left","Bottom"]).each(function(O,N){D[O]=K._num(J.css("padding"+N))
});
K.containerOffset=J.offset();
K.containerPosition=J.position();
K.containerSize={height:(J.innerHeight()-D[3]),width:(J.innerWidth()-D[1])};
L=K.containerOffset;
B=K.containerSize.height;
I=K.containerSize.width;
E=(K._hasScroll(F,"left")?F.scrollWidth:I);
M=(K._hasScroll(F)?F.scrollHeight:B);
K.parentData={element:F,left:L.left,top:L.top,width:E,height:M}
}},resize:function(C){var I,N,H,F,J=A(this).resizable("instance"),E=J.options,L=J.containerOffset,K=J.position,M=J._aspectRatio||C.shiftKey,B={top:0,left:0},D=J.containerElement,G=true;
if(D[0]!==document&&(/static/).test(D.css("position"))){B=L
}if(K.left<(J._helper?L.left:0)){J.size.width=J.size.width+(J._helper?(J.position.left-L.left):(J.position.left-B.left));
if(M){J.size.height=J.size.width/J.aspectRatio;
G=false
}J.position.left=E.helper?L.left:0
}if(K.top<(J._helper?L.top:0)){J.size.height=J.size.height+(J._helper?(J.position.top-L.top):J.position.top);
if(M){J.size.width=J.size.height*J.aspectRatio;
G=false
}J.position.top=J._helper?L.top:0
}H=J.containerElement.get(0)===J.element.parent().get(0);
F=/relative|absolute/.test(J.containerElement.css("position"));
if(H&&F){J.offset.left=J.parentData.left+J.position.left;
J.offset.top=J.parentData.top+J.position.top
}else{J.offset.left=J.element.offset().left;
J.offset.top=J.element.offset().top
}I=Math.abs(J.sizeDiff.width+(J._helper?J.offset.left-B.left:(J.offset.left-L.left)));
N=Math.abs(J.sizeDiff.height+(J._helper?J.offset.top-B.top:(J.offset.top-L.top)));
if(I+J.size.width>=J.parentData.width){J.size.width=J.parentData.width-I;
if(M){J.size.height=J.size.width/J.aspectRatio;
G=false
}}if(N+J.size.height>=J.parentData.height){J.size.height=J.parentData.height-N;
if(M){J.size.width=J.size.height*J.aspectRatio;
G=false
}}if(!G){J.position.left=J.prevPosition.left;
J.position.top=J.prevPosition.top;
J.size.width=J.prevSize.width;
J.size.height=J.prevSize.height
}},stop:function(){var G=A(this).resizable("instance"),C=G.options,H=G.containerOffset,B=G.containerPosition,D=G.containerElement,E=A(G.helper),J=E.offset(),I=E.outerWidth()-G.sizeDiff.width,F=E.outerHeight()-G.sizeDiff.height;
if(G._helper&&!C.animate&&(/relative/).test(D.css("position"))){A(this).css({left:J.left-B.left-H.left,width:I,height:F})
}if(G._helper&&!C.animate&&(/static/).test(D.css("position"))){A(this).css({left:J.left-B.left-H.left,width:I,height:F})
}}});
A.ui.plugin.add("resizable","alsoResize",{start:function(){var B=A(this).resizable("instance"),D=B.options,C=function(E){A(E).each(function(){var F=A(this);
F.data("ui-resizable-alsoresize",{width:parseInt(F.width(),10),height:parseInt(F.height(),10),left:parseInt(F.css("left"),10),top:parseInt(F.css("top"),10)})
})
};
if(typeof (D.alsoResize)==="object"&&!D.alsoResize.parentNode){if(D.alsoResize.length){D.alsoResize=D.alsoResize[0];
C(D.alsoResize)
}else{A.each(D.alsoResize,function(E){C(E)
})
}}else{C(D.alsoResize)
}},resize:function(D,F){var C=A(this).resizable("instance"),G=C.options,E=C.originalSize,I=C.originalPosition,H={height:(C.size.height-E.height)||0,width:(C.size.width-E.width)||0,top:(C.position.top-I.top)||0,left:(C.position.left-I.left)||0},B=function(J,K){A(J).each(function(){var N=A(this),O=A(this).data("ui-resizable-alsoresize"),M={},L=K&&K.length?K:N.parents(F.originalElement[0]).length?["width","height"]:["width","height","top","left"];
A.each(L,function(P,R){var Q=(O[R]||0)+(H[R]||0);
if(Q&&Q>=0){M[R]=Q||null
}});
N.css(M)
})
};
if(typeof (G.alsoResize)==="object"&&!G.alsoResize.nodeType){A.each(G.alsoResize,function(J,K){B(J,K)
})
}else{B(G.alsoResize)
}},stop:function(){A(this).removeData("resizable-alsoresize")
}});
A.ui.plugin.add("resizable","ghost",{start:function(){var C=A(this).resizable("instance"),D=C.options,B=C.size;
C.ghost=C.originalElement.clone();
C.ghost.css({opacity:0.25,display:"block",position:"relative",height:B.height,width:B.width,margin:0,left:0,top:0}).addClass("ui-resizable-ghost").addClass(typeof D.ghost==="string"?D.ghost:"");
C.ghost.appendTo(C.helper)
},resize:function(){var B=A(this).resizable("instance");
if(B.ghost){B.ghost.css({position:"relative",height:B.size.height,width:B.size.width})
}},stop:function(){var B=A(this).resizable("instance");
if(B.ghost&&B.helper){B.helper.get(0).removeChild(B.ghost.get(0))
}}});
A.ui.plugin.add("resizable","grid",{resize:function(){var E,J=A(this).resizable("instance"),N=J.options,H=J.size,I=J.originalSize,K=J.originalPosition,S=J.axis,B=typeof N.grid==="number"?[N.grid,N.grid]:N.grid,Q=(B[0]||1),P=(B[1]||1),G=Math.round((H.width-I.width)/Q)*Q,F=Math.round((H.height-I.height)/P)*P,L=I.width+G,O=I.height+F,D=N.maxWidth&&(N.maxWidth<L),M=N.maxHeight&&(N.maxHeight<O),R=N.minWidth&&(N.minWidth>L),C=N.minHeight&&(N.minHeight>O);
N.grid=B;
if(R){L+=Q
}if(C){O+=P
}if(D){L-=Q
}if(M){O-=P
}if(/^(se|s|e)$/.test(S)){J.size.width=L;
J.size.height=O
}else{if(/^(ne)$/.test(S)){J.size.width=L;
J.size.height=O;
J.position.top=K.top-F
}else{if(/^(sw)$/.test(S)){J.size.width=L;
J.size.height=O;
J.position.left=K.left-G
}else{if(O-P<=0||L-Q<=0){E=J._getPaddingPlusBorderDimensions(this)
}if(O-P>0){J.size.height=O;
J.position.top=K.top-F
}else{O=P-E.height;
J.size.height=O;
J.position.top=K.top+I.height-O
}if(L-Q>0){J.size.width=L;
J.position.left=K.left-G
}else{L=P-E.height;
J.size.width=L;
J.position.left=K.left+I.width-L
}}}}}});
return A.ui.resizable
}));