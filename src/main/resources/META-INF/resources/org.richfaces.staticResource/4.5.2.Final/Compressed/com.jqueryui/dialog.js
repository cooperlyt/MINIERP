/*
 * jQuery UI Dialog 1.11.2
 * http://jqueryui.com
 *
 * Copyright 2014 jQuery Foundation and other contributors
 * Released under the MIT license.
 * http://jquery.org/license
 *
 * http://api.jqueryui.com/dialog/
 */
(function(A){if(typeof define==="function"&&define.amd){define(["jquery","./core","./widget","./button","./draggable","./mouse","./position","./resizable"],A)
}else{A(jQuery)
}}(function(A){return A.widget("ui.dialog",{version:"1.11.2",options:{appendTo:"body",autoOpen:true,buttons:[],closeOnEscape:true,closeText:"Close",dialogClass:"",draggable:true,hide:null,height:"auto",maxHeight:null,maxWidth:null,minHeight:150,minWidth:150,modal:false,position:{my:"center",at:"center",of:window,collision:"fit",using:function(C){var B=A(this).css(C).offset().top;
if(B<0){A(this).css("top",C.top-B)
}}},resizable:true,show:null,title:null,width:300,beforeClose:null,close:null,drag:null,dragStart:null,dragStop:null,focus:null,open:null,resize:null,resizeStart:null,resizeStop:null},sizeRelatedOptions:{buttons:true,height:true,maxHeight:true,maxWidth:true,minHeight:true,minWidth:true,width:true},resizableRelatedOptions:{maxHeight:true,maxWidth:true,minHeight:true,minWidth:true},_create:function(){this.originalCss={display:this.element[0].style.display,width:this.element[0].style.width,minHeight:this.element[0].style.minHeight,maxHeight:this.element[0].style.maxHeight,height:this.element[0].style.height};
this.originalPosition={parent:this.element.parent(),index:this.element.parent().children().index(this.element)};
this.originalTitle=this.element.attr("title");
this.options.title=this.options.title||this.originalTitle;
this._createWrapper();
this.element.show().removeAttr("title").addClass("ui-dialog-content ui-widget-content").appendTo(this.uiDialog);
this._createTitlebar();
this._createButtonPane();
if(this.options.draggable&&A.fn.draggable){this._makeDraggable()
}if(this.options.resizable&&A.fn.resizable){this._makeResizable()
}this._isOpen=false;
this._trackFocus()
},_init:function(){if(this.options.autoOpen){this.open()
}},_appendTo:function(){var B=this.options.appendTo;
if(B&&(B.jquery||B.nodeType)){return A(B)
}return this.document.find(B||"body").eq(0)
},_destroy:function(){var C,B=this.originalPosition;
this._destroyOverlay();
this.element.removeUniqueId().removeClass("ui-dialog-content ui-widget-content").css(this.originalCss).detach();
this.uiDialog.stop(true,true).remove();
if(this.originalTitle){this.element.attr("title",this.originalTitle)
}C=B.parent.children().eq(B.index);
if(C.length&&C[0]!==this.element[0]){C.before(this.element)
}else{B.parent.append(this.element)
}},widget:function(){return this.uiDialog
},disable:A.noop,enable:A.noop,close:function(E){var D,C=this;
if(!this._isOpen||this._trigger("beforeClose",E)===false){return 
}this._isOpen=false;
this._focusedElement=null;
this._destroyOverlay();
this._untrackInstance();
if(!this.opener.filter(":focusable").focus().length){try{D=this.document[0].activeElement;
if(D&&D.nodeName.toLowerCase()!=="body"){A(D).blur()
}}catch(B){}}this._hide(this.uiDialog,this.options.hide,function(){C._trigger("close",E)
})
},isOpen:function(){return this._isOpen
},moveToTop:function(){this._moveToTop()
},_moveToTop:function(F,C){var E=false,B=this.uiDialog.siblings(".ui-front:visible").map(function(){return +A(this).css("z-index")
}).get(),D=Math.max.apply(null,B);
if(D>=+this.uiDialog.css("z-index")){this.uiDialog.css("z-index",D+1);
E=true
}if(E&&!C){this._trigger("focus",F)
}return E
},open:function(){var B=this;
if(this._isOpen){if(this._moveToTop()){this._focusTabbable()
}return 
}this._isOpen=true;
this.opener=A(this.document[0].activeElement);
this._size();
this._position();
this._createOverlay();
this._moveToTop(null,true);
if(this.overlay){this.overlay.css("z-index",this.uiDialog.css("z-index")-1)
}this._show(this.uiDialog,this.options.show,function(){B._focusTabbable();
B._trigger("focus")
});
this._makeFocusTarget();
this._trigger("open")
},_focusTabbable:function(){var B=this._focusedElement;
if(!B){B=this.element.find("[autofocus]")
}if(!B.length){B=this.element.find(":tabbable")
}if(!B.length){B=this.uiDialogButtonPane.find(":tabbable")
}if(!B.length){B=this.uiDialogTitlebarClose.filter(":tabbable")
}if(!B.length){B=this.uiDialog
}B.eq(0).focus()
},_keepFocus:function(B){function C(){var E=this.document[0].activeElement,D=this.uiDialog[0]===E||A.contains(this.uiDialog[0],E);
if(!D){this._focusTabbable()
}}B.preventDefault();
C.call(this);
this._delay(C)
},_createWrapper:function(){this.uiDialog=A("<div>").addClass("ui-dialog ui-widget ui-widget-content ui-corner-all ui-front "+this.options.dialogClass).hide().attr({tabIndex:-1,role:"dialog"}).appendTo(this._appendTo());
this._on(this.uiDialog,{keydown:function(D){if(this.options.closeOnEscape&&!D.isDefaultPrevented()&&D.keyCode&&D.keyCode===A.ui.keyCode.ESCAPE){D.preventDefault();
this.close(D);
return 
}if(D.keyCode!==A.ui.keyCode.TAB||D.isDefaultPrevented()){return 
}var C=this.uiDialog.find(":tabbable"),E=C.filter(":first"),B=C.filter(":last");
if((D.target===B[0]||D.target===this.uiDialog[0])&&!D.shiftKey){this._delay(function(){E.focus()
});
D.preventDefault()
}else{if((D.target===E[0]||D.target===this.uiDialog[0])&&D.shiftKey){this._delay(function(){B.focus()
});
D.preventDefault()
}}},mousedown:function(B){if(this._moveToTop(B)){this._focusTabbable()
}}});
if(!this.element.find("[aria-describedby]").length){this.uiDialog.attr({"aria-describedby":this.element.uniqueId().attr("id")})
}},_createTitlebar:function(){var B;
this.uiDialogTitlebar=A("<div>").addClass("ui-dialog-titlebar ui-widget-header ui-corner-all ui-helper-clearfix").prependTo(this.uiDialog);
this._on(this.uiDialogTitlebar,{mousedown:function(C){if(!A(C.target).closest(".ui-dialog-titlebar-close")){this.uiDialog.focus()
}}});
this.uiDialogTitlebarClose=A("<button type='button'></button>").button({label:this.options.closeText,icons:{primary:"ui-icon-closethick"},text:false}).addClass("ui-dialog-titlebar-close").appendTo(this.uiDialogTitlebar);
this._on(this.uiDialogTitlebarClose,{click:function(C){C.preventDefault();
this.close(C)
}});
B=A("<span>").uniqueId().addClass("ui-dialog-title").prependTo(this.uiDialogTitlebar);
this._title(B);
this.uiDialog.attr({"aria-labelledby":B.attr("id")})
},_title:function(B){if(!this.options.title){B.html("&#160;")
}B.text(this.options.title)
},_createButtonPane:function(){this.uiDialogButtonPane=A("<div>").addClass("ui-dialog-buttonpane ui-widget-content ui-helper-clearfix");
this.uiButtonSet=A("<div>").addClass("ui-dialog-buttonset").appendTo(this.uiDialogButtonPane);
this._createButtons()
},_createButtons:function(){var C=this,B=this.options.buttons;
this.uiDialogButtonPane.remove();
this.uiButtonSet.empty();
if(A.isEmptyObject(B)||(A.isArray(B)&&!B.length)){this.uiDialog.removeClass("ui-dialog-buttons");
return 
}A.each(B,function(D,E){var F,G;
E=A.isFunction(E)?{click:E,text:D}:E;
E=A.extend({type:"button"},E);
F=E.click;
E.click=function(){F.apply(C.element[0],arguments)
};
G={icons:E.icons,text:E.showText};
delete E.icons;
delete E.showText;
A("<button></button>",E).button(G).appendTo(C.uiButtonSet)
});
this.uiDialog.addClass("ui-dialog-buttons");
this.uiDialogButtonPane.appendTo(this.uiDialog)
},_makeDraggable:function(){var D=this,C=this.options;
function B(E){return{position:E.position,offset:E.offset}
}this.uiDialog.draggable({cancel:".ui-dialog-content, .ui-dialog-titlebar-close",handle:".ui-dialog-titlebar",containment:"document",start:function(E,F){A(this).addClass("ui-dialog-dragging");
D._blockFrames();
D._trigger("dragStart",E,B(F))
},drag:function(E,F){D._trigger("drag",E,B(F))
},stop:function(E,F){var H=F.offset.left-D.document.scrollLeft(),G=F.offset.top-D.document.scrollTop();
C.position={my:"left top",at:"left"+(H>=0?"+":"")+H+" top"+(G>=0?"+":"")+G,of:D.window};
A(this).removeClass("ui-dialog-dragging");
D._unblockFrames();
D._trigger("dragStop",E,B(F))
}})
},_makeResizable:function(){var G=this,E=this.options,F=E.resizable,B=this.uiDialog.css("position"),D=typeof F==="string"?F:"n,e,s,w,se,sw,ne,nw";
function C(H){return{originalPosition:H.originalPosition,originalSize:H.originalSize,position:H.position,size:H.size}
}this.uiDialog.resizable({cancel:".ui-dialog-content",containment:"document",alsoResize:this.element,maxWidth:E.maxWidth,maxHeight:E.maxHeight,minWidth:E.minWidth,minHeight:this._minHeight(),handles:D,start:function(H,I){A(this).addClass("ui-dialog-resizing");
G._blockFrames();
G._trigger("resizeStart",H,C(I))
},resize:function(H,I){G._trigger("resize",H,C(I))
},stop:function(H,I){var L=G.uiDialog.offset(),K=L.left-G.document.scrollLeft(),J=L.top-G.document.scrollTop();
E.height=G.uiDialog.height();
E.width=G.uiDialog.width();
E.position={my:"left top",at:"left"+(K>=0?"+":"")+K+" top"+(J>=0?"+":"")+J,of:G.window};
A(this).removeClass("ui-dialog-resizing");
G._unblockFrames();
G._trigger("resizeStop",H,C(I))
}}).css("position",B)
},_trackFocus:function(){this._on(this.widget(),{focusin:function(B){this._makeFocusTarget();
this._focusedElement=A(B.target)
}})
},_makeFocusTarget:function(){this._untrackInstance();
this._trackingInstances().unshift(this)
},_untrackInstance:function(){var C=this._trackingInstances(),B=A.inArray(this,C);
if(B!==-1){C.splice(B,1)
}},_trackingInstances:function(){var B=this.document.data("ui-dialog-instances");
if(!B){B=[];
this.document.data("ui-dialog-instances",B)
}return B
},_minHeight:function(){var B=this.options;
return B.height==="auto"?B.minHeight:Math.min(B.minHeight,B.height)
},_position:function(){var B=this.uiDialog.is(":visible");
if(!B){this.uiDialog.show()
}this.uiDialog.position(this.options.position);
if(!B){this.uiDialog.hide()
}},_setOptions:function(D){var E=this,C=false,B={};
A.each(D,function(F,G){E._setOption(F,G);
if(F in E.sizeRelatedOptions){C=true
}if(F in E.resizableRelatedOptions){B[F]=G
}});
if(C){this._size();
this._position()
}if(this.uiDialog.is(":data(ui-resizable)")){this.uiDialog.resizable("option",B)
}},_setOption:function(D,E){var C,F,B=this.uiDialog;
if(D==="dialogClass"){B.removeClass(this.options.dialogClass).addClass(E)
}if(D==="disabled"){return 
}this._super(D,E);
if(D==="appendTo"){this.uiDialog.appendTo(this._appendTo())
}if(D==="buttons"){this._createButtons()
}if(D==="closeText"){this.uiDialogTitlebarClose.button({label:""+E})
}if(D==="draggable"){C=B.is(":data(ui-draggable)");
if(C&&!E){B.draggable("destroy")
}if(!C&&E){this._makeDraggable()
}}if(D==="position"){this._position()
}if(D==="resizable"){F=B.is(":data(ui-resizable)");
if(F&&!E){B.resizable("destroy")
}if(F&&typeof E==="string"){B.resizable("option","handles",E)
}if(!F&&E!==false){this._makeResizable()
}}if(D==="title"){this._title(this.uiDialogTitlebar.find(".ui-dialog-title"))
}},_size:function(){var B,D,E,C=this.options;
this.element.show().css({width:"auto",minHeight:0,maxHeight:"none",height:0});
if(C.minWidth>C.width){C.width=C.minWidth
}B=this.uiDialog.css({height:"auto",width:C.width}).outerHeight();
D=Math.max(0,C.minHeight-B);
E=typeof C.maxHeight==="number"?Math.max(0,C.maxHeight-B):"none";
if(C.height==="auto"){this.element.css({minHeight:D,maxHeight:E,height:"auto"})
}else{this.element.height(Math.max(0,C.height-B))
}if(this.uiDialog.is(":data(ui-resizable)")){this.uiDialog.resizable("option","minHeight",this._minHeight())
}},_blockFrames:function(){this.iframeBlocks=this.document.find("iframe").map(function(){var B=A(this);
return A("<div>").css({position:"absolute",width:B.outerWidth(),height:B.outerHeight()}).appendTo(B.parent()).offset(B.offset())[0]
})
},_unblockFrames:function(){if(this.iframeBlocks){this.iframeBlocks.remove();
delete this.iframeBlocks
}},_allowInteraction:function(B){if(A(B.target).closest(".ui-dialog").length){return true
}return !!A(B.target).closest(".ui-datepicker").length
},_createOverlay:function(){if(!this.options.modal){return 
}var B=true;
this._delay(function(){B=false
});
if(!this.document.data("ui-dialog-overlays")){this._on(this.document,{focusin:function(C){if(B){return 
}if(!this._allowInteraction(C)){C.preventDefault();
this._trackingInstances()[0]._focusTabbable()
}}})
}this.overlay=A("<div>").addClass("ui-widget-overlay ui-front").appendTo(this._appendTo());
this._on(this.overlay,{mousedown:"_keepFocus"});
this.document.data("ui-dialog-overlays",(this.document.data("ui-dialog-overlays")||0)+1)
},_destroyOverlay:function(){if(!this.options.modal){return 
}if(this.overlay){var B=this.document.data("ui-dialog-overlays")-1;
if(!B){this.document.unbind("focusin").removeData("ui-dialog-overlays")
}else{this.document.data("ui-dialog-overlays",B)
}this.overlay.remove();
this.overlay=null
}}})
}));