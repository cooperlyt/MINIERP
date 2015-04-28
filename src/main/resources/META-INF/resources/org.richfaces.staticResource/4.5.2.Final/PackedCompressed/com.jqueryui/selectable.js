/*
 * jQuery UI Selectable 1.11.2
 * http://jqueryui.com
 *
 * Copyright 2014 jQuery Foundation and other contributors
 * Released under the MIT license.
 * http://jquery.org/license
 *
 * http://api.jqueryui.com/selectable/
 */
(function(A){if(typeof define==="function"&&define.amd){define(["jquery","./core","./mouse","./widget"],A)
}else{A(jQuery)
}}(function(A){return A.widget("ui.selectable",A.ui.mouse,{version:"1.11.2",options:{appendTo:"body",autoRefresh:true,distance:0,filter:"*",tolerance:"touch",selected:null,selecting:null,start:null,stop:null,unselected:null,unselecting:null},_create:function(){var C,B=this;
this.element.addClass("ui-selectable");
this.dragged=false;
this.refresh=function(){C=A(B.options.filter,B.element[0]);
C.addClass("ui-selectee");
C.each(function(){var D=A(this),E=D.offset();
A.data(this,"selectable-item",{element:this,$element:D,left:E.left,top:E.top,right:E.left+D.outerWidth(),bottom:E.top+D.outerHeight(),startselected:false,selected:D.hasClass("ui-selected"),selecting:D.hasClass("ui-selecting"),unselecting:D.hasClass("ui-unselecting")})
})
};
this.refresh();
this.selectees=C.addClass("ui-selectee");
this._mouseInit();
this.helper=A("<div class='ui-selectable-helper'></div>")
},_destroy:function(){this.selectees.removeClass("ui-selectee").removeData("selectable-item");
this.element.removeClass("ui-selectable ui-selectable-disabled");
this._mouseDestroy()
},_mouseStart:function(D){var C=this,B=this.options;
this.opos=[D.pageX,D.pageY];
if(this.options.disabled){return 
}this.selectees=A(B.filter,this.element[0]);
this._trigger("start",D);
A(B.appendTo).append(this.helper);
this.helper.css({left:D.pageX,top:D.pageY,width:0,height:0});
if(B.autoRefresh){this.refresh()
}this.selectees.filter(".ui-selected").each(function(){var E=A.data(this,"selectable-item");
E.startselected=true;
if(!D.metaKey&&!D.ctrlKey){E.$element.removeClass("ui-selected");
E.selected=false;
E.$element.addClass("ui-unselecting");
E.unselecting=true;
C._trigger("unselecting",D,{unselecting:E.element})
}});
A(D.target).parents().addBack().each(function(){var E,F=A.data(this,"selectable-item");
if(F){E=(!D.metaKey&&!D.ctrlKey)||!F.$element.hasClass("ui-selected");
F.$element.removeClass(E?"ui-unselecting":"ui-selected").addClass(E?"ui-selecting":"ui-unselecting");
F.unselecting=!E;
F.selecting=E;
F.selected=E;
if(E){C._trigger("selecting",D,{selecting:F.element})
}else{C._trigger("unselecting",D,{unselecting:F.element})
}return false
}})
},_mouseDrag:function(I){this.dragged=true;
if(this.options.disabled){return 
}var F,H=this,D=this.options,C=this.opos[0],G=this.opos[1],B=I.pageX,E=I.pageY;
if(C>B){F=B;
B=C;
C=F
}if(G>E){F=E;
E=G;
G=F
}this.helper.css({left:C,top:G,width:B-C,height:E-G});
this.selectees.each(function(){var J=A.data(this,"selectable-item"),K=false;
if(!J||J.element===H.element[0]){return 
}if(D.tolerance==="touch"){K=(!(J.left>B||J.right<C||J.top>E||J.bottom<G))
}else{if(D.tolerance==="fit"){K=(J.left>C&&J.right<B&&J.top>G&&J.bottom<E)
}}if(K){if(J.selected){J.$element.removeClass("ui-selected");
J.selected=false
}if(J.unselecting){J.$element.removeClass("ui-unselecting");
J.unselecting=false
}if(!J.selecting){J.$element.addClass("ui-selecting");
J.selecting=true;
H._trigger("selecting",I,{selecting:J.element})
}}else{if(J.selecting){if((I.metaKey||I.ctrlKey)&&J.startselected){J.$element.removeClass("ui-selecting");
J.selecting=false;
J.$element.addClass("ui-selected");
J.selected=true
}else{J.$element.removeClass("ui-selecting");
J.selecting=false;
if(J.startselected){J.$element.addClass("ui-unselecting");
J.unselecting=true
}H._trigger("unselecting",I,{unselecting:J.element})
}}if(J.selected){if(!I.metaKey&&!I.ctrlKey&&!J.startselected){J.$element.removeClass("ui-selected");
J.selected=false;
J.$element.addClass("ui-unselecting");
J.unselecting=true;
H._trigger("unselecting",I,{unselecting:J.element})
}}}});
return false
},_mouseStop:function(C){var B=this;
this.dragged=false;
A(".ui-unselecting",this.element[0]).each(function(){var D=A.data(this,"selectable-item");
D.$element.removeClass("ui-unselecting");
D.unselecting=false;
D.startselected=false;
B._trigger("unselected",C,{unselected:D.element})
});
A(".ui-selecting",this.element[0]).each(function(){var D=A.data(this,"selectable-item");
D.$element.removeClass("ui-selecting").addClass("ui-selected");
D.selecting=false;
D.selected=true;
D.startselected=true;
B._trigger("selected",C,{selected:D.element})
});
this._trigger("stop",C);
this.helper.remove();
return false
}})
}));