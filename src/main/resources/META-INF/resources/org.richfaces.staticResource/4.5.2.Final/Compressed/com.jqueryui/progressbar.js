/*
 * jQuery UI Progressbar 1.11.2
 * http://jqueryui.com
 *
 * Copyright 2014 jQuery Foundation and other contributors
 * Released under the MIT license.
 * http://jquery.org/license
 *
 * http://api.jqueryui.com/progressbar/
 */
(function(A){if(typeof define==="function"&&define.amd){define(["jquery","./core","./widget"],A)
}else{A(jQuery)
}}(function(A){return A.widget("ui.progressbar",{version:"1.11.2",options:{max:100,value:0,change:null,complete:null},min:0,_create:function(){this.oldValue=this.options.value=this._constrainedValue();
this.element.addClass("ui-progressbar ui-widget ui-widget-content ui-corner-all").attr({role:"progressbar","aria-valuemin":this.min});
this.valueDiv=A("<div class='ui-progressbar-value ui-widget-header ui-corner-left'></div>").appendTo(this.element);
this._refreshValue()
},_destroy:function(){this.element.removeClass("ui-progressbar ui-widget ui-widget-content ui-corner-all").removeAttr("role").removeAttr("aria-valuemin").removeAttr("aria-valuemax").removeAttr("aria-valuenow");
this.valueDiv.remove()
},value:function(B){if(B===undefined){return this.options.value
}this.options.value=this._constrainedValue(B);
this._refreshValue()
},_constrainedValue:function(B){if(B===undefined){B=this.options.value
}this.indeterminate=B===false;
if(typeof B!=="number"){B=0
}return this.indeterminate?false:Math.min(this.options.max,Math.max(this.min,B))
},_setOptions:function(B){var C=B.value;
delete B.value;
this._super(B);
this.options.value=this._constrainedValue(C);
this._refreshValue()
},_setOption:function(B,C){if(B==="max"){C=Math.max(this.min,C)
}if(B==="disabled"){this.element.toggleClass("ui-state-disabled",!!C).attr("aria-disabled",C)
}this._super(B,C)
},_percentage:function(){return this.indeterminate?100:100*(this.options.value-this.min)/(this.options.max-this.min)
},_refreshValue:function(){var C=this.options.value,B=this._percentage();
this.valueDiv.toggle(this.indeterminate||C>this.min).toggleClass("ui-corner-right",C===this.options.max).width(B.toFixed(0)+"%");
this.element.toggleClass("ui-progressbar-indeterminate",this.indeterminate);
if(this.indeterminate){this.element.removeAttr("aria-valuenow");
if(!this.overlayDiv){this.overlayDiv=A("<div class='ui-progressbar-overlay'></div>").appendTo(this.valueDiv)
}}else{this.element.attr({"aria-valuemax":this.options.max,"aria-valuenow":C});
if(this.overlayDiv){this.overlayDiv.remove();
this.overlayDiv=null
}}if(this.oldValue!==C){this.oldValue=C;
this._trigger("change")
}if(C===this.options.max){this._trigger("complete")
}}})
}));