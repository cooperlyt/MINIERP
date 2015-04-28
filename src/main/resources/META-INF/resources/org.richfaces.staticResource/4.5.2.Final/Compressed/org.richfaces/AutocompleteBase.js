(function(C,K){K.ui=K.ui||{};
K.ui.AutocompleteBase=function(U,V,S,T){P.constructor.call(this,U);
this.selectId=V;
this.fieldId=S;
this.options=C.extend({},O,T);
this.namespace=this.namespace||"."+K.Event.createNamespace(this.name,this.selectId);
this.currentValue=C(K.getDomElement(S)).val();
this.tempValue=this.getValue();
this.isChanged=this.tempValue.length!=0;
J.call(this)
};
K.BaseComponent.extend(K.ui.AutocompleteBase);
var P=K.ui.AutocompleteBase.$super;
var O={changeDelay:8};
var J=function(){var S={};
if(this.options.buttonId){S["mousedown"+this.namespace]=I;
S["mouseup"+this.namespace]=E;
K.Event.bindById(this.options.buttonId,S,this)
}S={};
S["focus"+this.namespace]=B;
S["blur"+this.namespace]=H;
S["click"+this.namespace]=D;
S["keydown"+this.namespace]=A;
S["change"+this.namespace]=function(T){if(this.focused){T.stopPropagation()
}};
K.Event.bindById(this.fieldId,S,this);
S={};
S["mousedown"+this.namespace]=N;
S["mouseup"+this.namespace]=E;
K.Event.bindById(this.selectId,S,this)
};
var N=function(){this.isMouseDown=true
};
var E=function(){K.getDomElement(this.fieldId).focus()
};
var I=function(S){this.isMouseDown=true;
if(this.timeoutId){window.clearTimeout(this.timeoutId);
this.timeoutId=null
}K.getDomElement(this.fieldId).focus();
if(this.isVisible){this.__hide(S)
}else{L.call(this,S)
}};
var B=function(S){if(!this.focused){this.__focusValue=this.getValue();
this.focused=true;
this.invokeEvent("focus",K.getDomElement(this.fieldId),S)
}};
var H=function(S){if(this.isMouseDown){K.getDomElement(this.fieldId).focus();
this.isMouseDown=false
}else{if(!this.isMouseDown){if(this.isVisible){var T=this;
this.timeoutId=window.setTimeout(function(){if(T.isVisible){T.__hide(S)
}},200)
}if(this.focused){this.focused=false;
this.invokeEvent("blur",K.getDomElement(this.fieldId),S);
if(this.__focusValue!=this.getValue()){this.invokeEvent("change",K.getDomElement(this.fieldId),S)
}}}}};
var D=function(S){};
var M=function(T){if(this.isChanged){if(this.getValue()==this.tempValue){return 
}}this.isChanged=false;
var U=this.getValue();
var S=U!=this.currentValue;
if(T.keyCode==K.KEYS.LEFT||T.keyCode==K.KEYS.RIGHT||S){if(S){this.currentValue=this.getValue();
this.__onChangeValue(T,undefined,(!this.isVisible?this.__show:undefined))
}else{if(this.isVisible){this.__onChangeValue(T)
}}}};
var L=function(S){if(this.isChanged){this.isChanged=false;
M.call(this,{})
}else{!this.__updateState(S)&&this.__show(S)
}};
var A=function(S){switch(S.keyCode){case K.KEYS.UP:S.preventDefault();
if(this.isVisible){this.__onKeyUp(S)
}break;
case K.KEYS.DOWN:S.preventDefault();
if(this.isVisible){this.__onKeyDown(S)
}else{L.call(this,S)
}break;
case K.KEYS.PAGEUP:if(this.isVisible){S.preventDefault();
this.__onPageUp(S)
}break;
case K.KEYS.PAGEDOWN:if(this.isVisible){S.preventDefault();
this.__onPageDown(S)
}break;
case K.KEYS.HOME:if(this.isVisible){S.preventDefault();
this.__onKeyHome(S)
}break;
case K.KEYS.END:if(this.isVisible){S.preventDefault();
this.__onKeyEnd(S)
}break;
case K.KEYS.RETURN:if(this.isVisible){S.preventDefault();
this.__onEnter(S);
this.__hide(S);
return false
}break;
case K.KEYS.ESC:this.__hide(S);
break;
default:if(!this.options.selectOnly){var T=this;
window.clearTimeout(this.changeTimerId);
this.changeTimerId=window.setTimeout(function(){M.call(T,S)
},this.options.changeDelay)
}break
}};
var Q=function(T){if(!this.isVisible){if(this.__onBeforeShow(T)!=false){this.scrollElements=K.Event.bindScrollEventHandlers(this.selectId,this.__hide,this,this.namespace);
var S=K.getDomElement(this.selectId);
if(this.options.attachToBody){this.parentElement=S.parentNode;
document.body.appendChild(S)
}C(S).setPosition({id:this.fieldId},{type:"DROPDOWN"}).show();
this.isVisible=true;
this.__onShow(T)
}}};
var G=function(S){if(this.isVisible){this.__conceal();
this.isVisible=false;
this.__onHide(S)
}};
var R=function(){if(this.isVisible){if(this.scrollElements){K.Event.unbindScrollEventHandlers(this.scrollElements,this);
this.scrollElements=null
}C(K.getDomElement(this.selectId)).hide();
if(this.options.attachToBody&&this.parentElement){this.parentElement.appendChild(K.getDomElement(this.selectId));
this.parentElement=null
}}};
var F=function(S){if(this.fieldId){K.getDomElement(this.fieldId).value=S;
return S
}else{return""
}};
C.extend(K.ui.AutocompleteBase.prototype,(function(){return{name:"AutocompleteBase",showPopup:function(S){if(!this.focused){K.getDomElement(this.fieldId).focus()
}L.call(this,S)
},hidePopup:function(S){this.__hide(S)
},getNamespace:function(){return this.namespace
},getValue:function(){return this.fieldId?K.getDomElement(this.fieldId).value:""
},setValue:function(S){if(S==this.currentValue){return 
}F.call(this,S);
this.isChanged=true
},__updateInputValue:F,__show:Q,__hide:G,__conceal:R,__onChangeValue:function(S){},__onKeyUp:function(S){},__onKeyDown:function(S){},__onPageUp:function(S){},__onPageDown:function(S){},__onKeyHome:function(S){},__onKeyEnd:function(S){},__onBeforeShow:function(S){},__onShow:function(S){},__onHide:function(S){},destroy:function(){this.parentNode=null;
if(this.scrollElements){K.Event.unbindScrollEventHandlers(this.scrollElements,this);
this.scrollElements=null
}this.options.buttonId&&K.Event.unbindById(this.options.buttonId,this.namespace);
K.Event.unbindById(this.fieldId,this.namespace);
K.Event.unbindById(this.selectId,this.namespace);
P.destroy.call(this)
}}
})())
})(RichFaces.jQuery,RichFaces);