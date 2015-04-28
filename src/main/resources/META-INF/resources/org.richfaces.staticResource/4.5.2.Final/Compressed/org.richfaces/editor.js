(function(D,C){C.ui=C.ui||{};
var A={toolbar:"Basic",skin:"moono",readonly:false,style:"",styleClass:"",editorStyle:"",editorClass:"",width:"100%",height:"200px"};
var B=["key","paste","undo","redo"];
C.ui.Editor=function(H,G,F){E.constructor.call(this,H);
this.options=D.extend({},A,G);
this.componentId=H;
this.textareaId=H+":inp";
this.editorElementId="cke_"+this.textareaId;
this.valueChanged=false;
this.dirtyState=false;
this.config=D.extend({},F);
this.attachToDom(this.componentId);
D(document).ready(D.proxy(this.__initializationHandler,this));
C.Event.bindById(this.__getTextarea(),"init",this.options.oninit,this);
C.Event.bindById(this.__getTextarea(),"dirty",this.options.ondirty,this)
};
C.BaseComponent.extend(C.ui.Editor);
var E=C.ui.Editor.$super;
D.extend(C.ui.Editor.prototype,{name:"Editor",__initializationHandler:function(){this.ckeditor=CKEDITOR.replace(this.textareaId,this.__getConfiguration());
if(this.__getForm()){this.__updateTextareaHandlerWrapper=C.Event.bind(this.__getForm(),"ajaxsubmit",D.proxy(this.__updateTextareaHandler,this))
}this.ckeditor.on("instanceReady",D.proxy(this.__instanceReadyHandler,this));
this.ckeditor.on("blur",D.proxy(this.__blurHandler,this));
this.ckeditor.on("focus",D.proxy(this.__focusHandler,this));
for(var F in B){this.ckeditor.on(B[F],D.proxy(this.__checkDirtyHandlerWithDelay,this))
}this.dirtyCheckingInterval=window.setInterval(D.proxy(this.__checkDirtyHandler,this),100)
},__checkDirtyHandlerWithDelay:function(){window.setTimeout(D.proxy(this.__checkDirtyHandler,this),0)
},__checkDirtyHandler:function(){if(this.ckeditor.checkDirty()){this.dirtyState=true;
this.valueChanged=true;
this.ckeditor.resetDirty();
this.__dirtyHandler()
}},__dirtyHandler:function(){this.invokeEvent.call(this,"dirty",document.getElementById(this.textareaId))
},__updateTextareaHandler:function(){this.ckeditor.updateElement()
},__instanceReadyHandler:function(F){this.__setupStyling();
this.__setupPassThroughAttributes();
this.invokeEvent.call(this,"init",document.getElementById(this.textareaId),F)
},__blurHandler:function(F){this.invokeEvent.call(this,"blur",document.getElementById(this.textareaId),F);
if(this.isDirty()){this.valueChanged=true;
this.__changeHandler()
}this.dirtyState=false
},__focusHandler:function(F){this.invokeEvent.call(this,"focus",document.getElementById(this.textareaId),F)
},__changeHandler:function(F){this.invokeEvent.call(this,"change",document.getElementById(this.textareaId),F)
},__getTextarea:function(){return D(document.getElementById(this.textareaId))
},__getForm:function(){return D("form").has(this.__getTextarea()).get(0)
},__getConfiguration:function(){var F=this.__getTextarea();
return D.extend({skin:this.options.skin,toolbar:this.__getToolbar(),readOnly:F.attr("readonly")||this.options.readonly,width:this.__resolveUnits(this.options.width),height:this.__resolveUnits(this.options.height),bodyClass:"rf-ed-b",defaultLanguage:this.options.lang,contentsLanguage:this.options.lang},this.config)
},__setupStyling:function(){var H=D(document.getElementById(this.editorElementId));
if(!H.hasClass("rf-ed")){H.addClass("rf-ed")
}var F=D.trim(this.options.styleClass+" "+this.options.editorClass);
if(this.initialStyle==undefined){this.initialStyle=H.attr("style")
}var G=this.__concatStyles(this.initialStyle,this.options.style,this.options.editorStyle);
if(this.oldStyleClass!==F){if(this.oldStyleClass){H.removeClass(this.oldStyleClass)
}H.addClass(F);
this.oldStyleClass=F
}if(this.oldStyle!==G){H.attr("style",G);
this.oldStyle=G
}},__setupPassThroughAttributes:function(){var F=this.__getTextarea();
var G=D(document.getElementById(this.editorElementId));
G.attr("title",F.attr("title"))
},__concatStyles:function(){var F="";
for(var G=0;
G<arguments.length;
G++){var H=D.trim(arguments[G]);
if(H){F=F+H+"; "
}}return F
},__getToolbar:function(){var G=this.options.toolbar;
var F=G.toLowerCase();
if(F==="basic"){return"Basic"
}if(F==="full"){return"Full"
}return G
},__setOptions:function(F){this.options=D.extend({},A,F)
},__resolveUnits:function(F){var F=D.trim(F);
if(F.match(/^[0-9]+$/)){return F+"px"
}else{return F
}},getEditor:function(){return this.ckeditor
},setValue:function(F){this.ckeditor.setData(F,D.proxy(function(){this.valueChanged=false;
this.dirtyState=false;
this.ckeditor.resetDirty()
},this))
},getValue:function(){return this.ckeditor.getData()
},getInput:function(){return document.getElementById(this.textareaId)
},focus:function(){this.ckeditor.focus()
},blur:function(){this.ckeditor.focusManager.blur(true)
},isFocused:function(){return this.ckeditor.focusManager.hasFocus
},isDirty:function(){return this.dirtyState||this.ckeditor.checkDirty()
},isValueChanged:function(){return this.valueChanged||this.isDirty()
},setReadOnly:function(F){this.ckeditor.setReadOnly(F!==false)
},isReadOnly:function(){return this.ckeditor.readOnly
},destroy:function(){window.clearInterval(this.dirtyCheckingInterval);
if(this.__getForm()){C.Event.unbind(this.__getForm(),"ajaxsubmit",this.__updateTextareaHandlerWrapper)
}if(this.ckeditor){this.ckeditor.destroy();
this.ckeditor=null
}this.__getTextarea().show();
E.destroy.call(this)
}})
})(RichFaces.jQuery,RichFaces);