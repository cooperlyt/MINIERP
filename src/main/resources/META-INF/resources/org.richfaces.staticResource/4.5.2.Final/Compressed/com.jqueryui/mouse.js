/*
 * jQuery UI Mouse 1.11.2
 * http://jqueryui.com
 *
 * Copyright 2014 jQuery Foundation and other contributors
 * Released under the MIT license.
 * http://jquery.org/license
 *
 * http://api.jqueryui.com/mouse/
 */
(function(A){if(typeof define==="function"&&define.amd){define(["jquery","./widget"],A)
}else{A(jQuery)
}}(function(B){var A=false;
B(document).mouseup(function(){A=false
});
return B.widget("ui.mouse",{version:"1.11.2",options:{cancel:"input,textarea,button,select,option",distance:1,delay:0},_mouseInit:function(){var C=this;
this.element.bind("mousedown."+this.widgetName,function(D){return C._mouseDown(D)
}).bind("click."+this.widgetName,function(D){if(true===B.data(D.target,C.widgetName+".preventClickEvent")){B.removeData(D.target,C.widgetName+".preventClickEvent");
D.stopImmediatePropagation();
return false
}});
this.started=false
},_mouseDestroy:function(){this.element.unbind("."+this.widgetName);
if(this._mouseMoveDelegate){this.document.unbind("mousemove."+this.widgetName,this._mouseMoveDelegate).unbind("mouseup."+this.widgetName,this._mouseUpDelegate)
}},_mouseDown:function(E){if(A){return 
}this._mouseMoved=false;
(this._mouseStarted&&this._mouseUp(E));
this._mouseDownEvent=E;
var D=this,F=(E.which===1),C=(typeof this.options.cancel==="string"&&E.target.nodeName?B(E.target).closest(this.options.cancel).length:false);
if(!F||C||!this._mouseCapture(E)){return true
}this.mouseDelayMet=!this.options.delay;
if(!this.mouseDelayMet){this._mouseDelayTimer=setTimeout(function(){D.mouseDelayMet=true
},this.options.delay)
}if(this._mouseDistanceMet(E)&&this._mouseDelayMet(E)){this._mouseStarted=(this._mouseStart(E)!==false);
if(!this._mouseStarted){E.preventDefault();
return true
}}if(true===B.data(E.target,this.widgetName+".preventClickEvent")){B.removeData(E.target,this.widgetName+".preventClickEvent")
}this._mouseMoveDelegate=function(G){return D._mouseMove(G)
};
this._mouseUpDelegate=function(G){return D._mouseUp(G)
};
this.document.bind("mousemove."+this.widgetName,this._mouseMoveDelegate).bind("mouseup."+this.widgetName,this._mouseUpDelegate);
E.preventDefault();
A=true;
return true
},_mouseMove:function(C){if(this._mouseMoved){if(B.ui.ie&&(!document.documentMode||document.documentMode<9)&&!C.button){return this._mouseUp(C)
}else{if(!C.which){return this._mouseUp(C)
}}}if(C.which||C.button){this._mouseMoved=true
}if(this._mouseStarted){this._mouseDrag(C);
return C.preventDefault()
}if(this._mouseDistanceMet(C)&&this._mouseDelayMet(C)){this._mouseStarted=(this._mouseStart(this._mouseDownEvent,C)!==false);
(this._mouseStarted?this._mouseDrag(C):this._mouseUp(C))
}return !this._mouseStarted
},_mouseUp:function(C){this.document.unbind("mousemove."+this.widgetName,this._mouseMoveDelegate).unbind("mouseup."+this.widgetName,this._mouseUpDelegate);
if(this._mouseStarted){this._mouseStarted=false;
if(C.target===this._mouseDownEvent.target){B.data(C.target,this.widgetName+".preventClickEvent",true)
}this._mouseStop(C)
}A=false;
return false
},_mouseDistanceMet:function(C){return(Math.max(Math.abs(this._mouseDownEvent.pageX-C.pageX),Math.abs(this._mouseDownEvent.pageY-C.pageY))>=this.options.distance)
},_mouseDelayMet:function(){return this.mouseDelayMet
},_mouseStart:function(){},_mouseDrag:function(){},_mouseStop:function(){},_mouseCapture:function(){return true
}})
}));