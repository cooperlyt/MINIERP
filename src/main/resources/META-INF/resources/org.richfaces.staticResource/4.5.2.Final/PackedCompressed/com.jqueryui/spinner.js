/*
 * jQuery UI Spinner 1.11.2
 * http://jqueryui.com
 *
 * Copyright 2014 jQuery Foundation and other contributors
 * Released under the MIT license.
 * http://jquery.org/license
 *
 * http://api.jqueryui.com/spinner/
 */
(function(A){if(typeof define==="function"&&define.amd){define(["jquery","./core","./widget","./button"],A)
}else{A(jQuery)
}}(function(B){function A(C){return function(){var D=this.element.val();
C.apply(this,arguments);
this._refresh();
if(D!==this.element.val()){this._trigger("change")
}}
}return B.widget("ui.spinner",{version:"1.11.2",defaultElement:"<input>",widgetEventPrefix:"spin",options:{culture:null,icons:{down:"ui-icon-triangle-1-s",up:"ui-icon-triangle-1-n"},incremental:true,max:null,min:null,numberFormat:null,page:10,step:1,change:null,spin:null,start:null,stop:null},_create:function(){this._setOption("max",this.options.max);
this._setOption("min",this.options.min);
this._setOption("step",this.options.step);
if(this.value()!==""){this._value(this.element.val(),true)
}this._draw();
this._on(this._events);
this._refresh();
this._on(this.window,{beforeunload:function(){this.element.removeAttr("autocomplete")
}})
},_getCreateOptions:function(){var C={},D=this.element;
B.each(["min","max","step"],function(E,F){var G=D.attr(F);
if(G!==undefined&&G.length){C[F]=G
}});
return C
},_events:{keydown:function(C){if(this._start(C)&&this._keydown(C)){C.preventDefault()
}},keyup:"_stop",focus:function(){this.previous=this.element.val()
},blur:function(C){if(this.cancelBlur){delete this.cancelBlur;
return 
}this._stop();
this._refresh();
if(this.previous!==this.element.val()){this._trigger("change",C)
}},mousewheel:function(C,D){if(!D){return 
}if(!this.spinning&&!this._start(C)){return false
}this._spin((D>0?1:-1)*this.options.step,C);
clearTimeout(this.mousewheelTimer);
this.mousewheelTimer=this._delay(function(){if(this.spinning){this._stop(C)
}},100);
C.preventDefault()
},"mousedown .ui-spinner-button":function(D){var C;
C=this.element[0]===this.document[0].activeElement?this.previous:this.element.val();
function E(){var F=this.element[0]===this.document[0].activeElement;
if(!F){this.element.focus();
this.previous=C;
this._delay(function(){this.previous=C
})
}}D.preventDefault();
E.call(this);
this.cancelBlur=true;
this._delay(function(){delete this.cancelBlur;
E.call(this)
});
if(this._start(D)===false){return 
}this._repeat(null,B(D.currentTarget).hasClass("ui-spinner-up")?1:-1,D)
},"mouseup .ui-spinner-button":"_stop","mouseenter .ui-spinner-button":function(C){if(!B(C.currentTarget).hasClass("ui-state-active")){return 
}if(this._start(C)===false){return false
}this._repeat(null,B(C.currentTarget).hasClass("ui-spinner-up")?1:-1,C)
},"mouseleave .ui-spinner-button":"_stop"},_draw:function(){var C=this.uiSpinner=this.element.addClass("ui-spinner-input").attr("autocomplete","off").wrap(this._uiSpinnerHtml()).parent().append(this._buttonHtml());
this.element.attr("role","spinbutton");
this.buttons=C.find(".ui-spinner-button").attr("tabIndex",-1).button().removeClass("ui-corner-all");
if(this.buttons.height()>Math.ceil(C.height()*0.5)&&C.height()>0){C.height(C.height())
}if(this.options.disabled){this.disable()
}},_keydown:function(D){var C=this.options,E=B.ui.keyCode;
switch(D.keyCode){case E.UP:this._repeat(null,1,D);
return true;
case E.DOWN:this._repeat(null,-1,D);
return true;
case E.PAGE_UP:this._repeat(null,C.page,D);
return true;
case E.PAGE_DOWN:this._repeat(null,-C.page,D);
return true
}return false
},_uiSpinnerHtml:function(){return"<span class='ui-spinner ui-widget ui-widget-content ui-corner-all'></span>"
},_buttonHtml:function(){return"<a class='ui-spinner-button ui-spinner-up ui-corner-tr'><span class='ui-icon "+this.options.icons.up+"'>&#9650;</span></a><a class='ui-spinner-button ui-spinner-down ui-corner-br'><span class='ui-icon "+this.options.icons.down+"'>&#9660;</span></a>"
},_start:function(C){if(!this.spinning&&this._trigger("start",C)===false){return false
}if(!this.counter){this.counter=1
}this.spinning=true;
return true
},_repeat:function(D,C,E){D=D||500;
clearTimeout(this.timer);
this.timer=this._delay(function(){this._repeat(40,C,E)
},D);
this._spin(C*this.options.step,E)
},_spin:function(D,C){var E=this.value()||0;
if(!this.counter){this.counter=1
}E=this._adjustValue(E+D*this._increment(this.counter));
if(!this.spinning||this._trigger("spin",C,{value:E})!==false){this._value(E);
this.counter++
}},_increment:function(C){var D=this.options.incremental;
if(D){return B.isFunction(D)?D(C):Math.floor(C*C*C/50000-C*C/500+17*C/200+1)
}return 1
},_precision:function(){var C=this._precisionOf(this.options.step);
if(this.options.min!==null){C=Math.max(C,this._precisionOf(this.options.min))
}return C
},_precisionOf:function(D){var E=D.toString(),C=E.indexOf(".");
return C===-1?0:E.length-C-1
},_adjustValue:function(E){var D,F,C=this.options;
D=C.min!==null?C.min:0;
F=E-D;
F=Math.round(F/C.step)*C.step;
E=D+F;
E=parseFloat(E.toFixed(this._precision()));
if(C.max!==null&&E>C.max){return C.max
}if(C.min!==null&&E<C.min){return C.min
}return E
},_stop:function(C){if(!this.spinning){return 
}clearTimeout(this.timer);
clearTimeout(this.mousewheelTimer);
this.counter=0;
this.spinning=false;
this._trigger("stop",C)
},_setOption:function(C,D){if(C==="culture"||C==="numberFormat"){var E=this._parse(this.element.val());
this.options[C]=D;
this.element.val(this._format(E));
return 
}if(C==="max"||C==="min"||C==="step"){if(typeof D==="string"){D=this._parse(D)
}}if(C==="icons"){this.buttons.first().find(".ui-icon").removeClass(this.options.icons.up).addClass(D.up);
this.buttons.last().find(".ui-icon").removeClass(this.options.icons.down).addClass(D.down)
}this._super(C,D);
if(C==="disabled"){this.widget().toggleClass("ui-state-disabled",!!D);
this.element.prop("disabled",!!D);
this.buttons.button(D?"disable":"enable")
}},_setOptions:A(function(C){this._super(C)
}),_parse:function(C){if(typeof C==="string"&&C!==""){C=window.Globalize&&this.options.numberFormat?Globalize.parseFloat(C,10,this.options.culture):+C
}return C===""||isNaN(C)?null:C
},_format:function(C){if(C===""){return""
}return window.Globalize&&this.options.numberFormat?Globalize.format(C,this.options.numberFormat,this.options.culture):C
},_refresh:function(){this.element.attr({"aria-valuemin":this.options.min,"aria-valuemax":this.options.max,"aria-valuenow":this._parse(this.element.val())})
},isValid:function(){var C=this.value();
if(C===null){return false
}return C===this._adjustValue(C)
},_value:function(E,C){var D;
if(E!==""){D=this._parse(E);
if(D!==null){if(!C){D=this._adjustValue(D)
}E=this._format(D)
}}this.element.val(E);
this._refresh()
},_destroy:function(){this.element.removeClass("ui-spinner-input").prop("disabled",false).removeAttr("autocomplete").removeAttr("role").removeAttr("aria-valuemin").removeAttr("aria-valuemax").removeAttr("aria-valuenow");
this.uiSpinner.replaceWith(this.element)
},stepUp:A(function(C){this._stepUp(C)
}),_stepUp:function(C){if(this._start()){this._spin((C||1)*this.options.step);
this._stop()
}},stepDown:A(function(C){this._stepDown(C)
}),_stepDown:function(C){if(this._start()){this._spin((C||1)*-this.options.step);
this._stop()
}},pageUp:A(function(C){this._stepUp((C||1)*this.options.page)
}),pageDown:A(function(C){this._stepDown((C||1)*this.options.page)
}),value:function(C){if(!arguments.length){return this._parse(this.element.val())
}A(this._value).call(this,C)
},widget:function(){return this.uiSpinner
}})
}));