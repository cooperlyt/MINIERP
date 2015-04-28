/*
 * jQuery UI Button 1.11.2
 * http://jqueryui.com
 *
 * Copyright 2014 jQuery Foundation and other contributors
 * Released under the MIT license.
 * http://jquery.org/license
 *
 * http://api.jqueryui.com/button/
 */
(function(A){if(typeof define==="function"&&define.amd){define(["jquery","./core","./widget"],A)
}else{A(jQuery)
}}(function(E){var C,B="ui-button ui-widget ui-state-default ui-corner-all",F="ui-button-icons-only ui-button-icon-only ui-button-text-icons ui-button-text-icon-primary ui-button-text-icon-secondary ui-button-text-only",D=function(){var G=E(this);
setTimeout(function(){G.find(":ui-button").button("refresh")
},1)
},A=function(H){var G=H.name,I=H.form,J=E([]);
if(G){G=G.replace(/'/g,"\\'");
if(I){J=E(I).find("[name='"+G+"'][type=radio]")
}else{J=E("[name='"+G+"'][type=radio]",H.ownerDocument).filter(function(){return !this.form
})
}}return J
};
E.widget("ui.button",{version:"1.11.2",defaultElement:"<button>",options:{disabled:null,text:true,label:null,icons:{primary:null,secondary:null}},_create:function(){this.element.closest("form").unbind("reset"+this.eventNamespace).bind("reset"+this.eventNamespace,D);
if(typeof this.options.disabled!=="boolean"){this.options.disabled=!!this.element.prop("disabled")
}else{this.element.prop("disabled",this.options.disabled)
}this._determineButtonType();
this.hasTitle=!!this.buttonElement.attr("title");
var I=this,G=this.options,J=this.type==="checkbox"||this.type==="radio",H=!J?"ui-state-active":"";
if(G.label===null){G.label=(this.type==="input"?this.buttonElement.val():this.buttonElement.html())
}this._hoverable(this.buttonElement);
this.buttonElement.addClass(B).attr("role","button").bind("mouseenter"+this.eventNamespace,function(){if(G.disabled){return 
}if(this===C){E(this).addClass("ui-state-active")
}}).bind("mouseleave"+this.eventNamespace,function(){if(G.disabled){return 
}E(this).removeClass(H)
}).bind("click"+this.eventNamespace,function(K){if(G.disabled){K.preventDefault();
K.stopImmediatePropagation()
}});
this._on({focus:function(){this.buttonElement.addClass("ui-state-focus")
},blur:function(){this.buttonElement.removeClass("ui-state-focus")
}});
if(J){this.element.bind("change"+this.eventNamespace,function(){I.refresh()
})
}if(this.type==="checkbox"){this.buttonElement.bind("click"+this.eventNamespace,function(){if(G.disabled){return false
}})
}else{if(this.type==="radio"){this.buttonElement.bind("click"+this.eventNamespace,function(){if(G.disabled){return false
}E(this).addClass("ui-state-active");
I.buttonElement.attr("aria-pressed","true");
var K=I.element[0];
A(K).not(K).map(function(){return E(this).button("widget")[0]
}).removeClass("ui-state-active").attr("aria-pressed","false")
})
}else{this.buttonElement.bind("mousedown"+this.eventNamespace,function(){if(G.disabled){return false
}E(this).addClass("ui-state-active");
C=this;
I.document.one("mouseup",function(){C=null
})
}).bind("mouseup"+this.eventNamespace,function(){if(G.disabled){return false
}E(this).removeClass("ui-state-active")
}).bind("keydown"+this.eventNamespace,function(K){if(G.disabled){return false
}if(K.keyCode===E.ui.keyCode.SPACE||K.keyCode===E.ui.keyCode.ENTER){E(this).addClass("ui-state-active")
}}).bind("keyup"+this.eventNamespace+" blur"+this.eventNamespace,function(){E(this).removeClass("ui-state-active")
});
if(this.buttonElement.is("a")){this.buttonElement.keyup(function(K){if(K.keyCode===E.ui.keyCode.SPACE){E(this).click()
}})
}}}this._setOption("disabled",G.disabled);
this._resetButton()
},_determineButtonType:function(){var G,I,H;
if(this.element.is("[type=checkbox]")){this.type="checkbox"
}else{if(this.element.is("[type=radio]")){this.type="radio"
}else{if(this.element.is("input")){this.type="input"
}else{this.type="button"
}}}if(this.type==="checkbox"||this.type==="radio"){G=this.element.parents().last();
I="label[for='"+this.element.attr("id")+"']";
this.buttonElement=G.find(I);
if(!this.buttonElement.length){G=G.length?G.siblings():this.element.siblings();
this.buttonElement=G.filter(I);
if(!this.buttonElement.length){this.buttonElement=G.find(I)
}}this.element.addClass("ui-helper-hidden-accessible");
H=this.element.is(":checked");
if(H){this.buttonElement.addClass("ui-state-active")
}this.buttonElement.prop("aria-pressed",H)
}else{this.buttonElement=this.element
}},widget:function(){return this.buttonElement
},_destroy:function(){this.element.removeClass("ui-helper-hidden-accessible");
this.buttonElement.removeClass(B+" ui-state-active "+F).removeAttr("role").removeAttr("aria-pressed").html(this.buttonElement.find(".ui-button-text").html());
if(!this.hasTitle){this.buttonElement.removeAttr("title")
}},_setOption:function(G,H){this._super(G,H);
if(G==="disabled"){this.widget().toggleClass("ui-state-disabled",!!H);
this.element.prop("disabled",!!H);
if(H){if(this.type==="checkbox"||this.type==="radio"){this.buttonElement.removeClass("ui-state-focus")
}else{this.buttonElement.removeClass("ui-state-focus ui-state-active")
}}return 
}this._resetButton()
},refresh:function(){var G=this.element.is("input, button")?this.element.is(":disabled"):this.element.hasClass("ui-button-disabled");
if(G!==this.options.disabled){this._setOption("disabled",G)
}if(this.type==="radio"){A(this.element[0]).each(function(){if(E(this).is(":checked")){E(this).button("widget").addClass("ui-state-active").attr("aria-pressed","true")
}else{E(this).button("widget").removeClass("ui-state-active").attr("aria-pressed","false")
}})
}else{if(this.type==="checkbox"){if(this.element.is(":checked")){this.buttonElement.addClass("ui-state-active").attr("aria-pressed","true")
}else{this.buttonElement.removeClass("ui-state-active").attr("aria-pressed","false")
}}}},_resetButton:function(){if(this.type==="input"){if(this.options.label){this.element.val(this.options.label)
}return 
}var K=this.buttonElement.removeClass(F),I=E("<span></span>",this.document[0]).addClass("ui-button-text").html(this.options.label).appendTo(K.empty()).text(),H=this.options.icons,G=H.primary&&H.secondary,J=[];
if(H.primary||H.secondary){if(this.options.text){J.push("ui-button-text-icon"+(G?"s":(H.primary?"-primary":"-secondary")))
}if(H.primary){K.prepend("<span class='ui-button-icon-primary ui-icon "+H.primary+"'></span>")
}if(H.secondary){K.append("<span class='ui-button-icon-secondary ui-icon "+H.secondary+"'></span>")
}if(!this.options.text){J.push(G?"ui-button-icons-only":"ui-button-icon-only");
if(!this.hasTitle){K.attr("title",E.trim(I))
}}}else{J.push("ui-button-text-only")
}K.addClass(J.join(" "))
}});
E.widget("ui.buttonset",{version:"1.11.2",options:{items:"button, input[type=button], input[type=submit], input[type=reset], input[type=checkbox], input[type=radio], a, :data(ui-button)"},_create:function(){this.element.addClass("ui-buttonset")
},_init:function(){this.refresh()
},_setOption:function(G,H){if(G==="disabled"){this.buttons.button("option",G,H)
}this._super(G,H)
},refresh:function(){var H=this.element.css("direction")==="rtl",G=this.element.find(this.options.items),I=G.filter(":ui-button");
G.not(":ui-button").button();
I.button("refresh");
this.buttons=G.map(function(){return E(this).button("widget")[0]
}).removeClass("ui-corner-all ui-corner-left ui-corner-right").filter(":first").addClass(H?"ui-corner-right":"ui-corner-left").end().filter(":last").addClass(H?"ui-corner-left":"ui-corner-right").end().end()
},_destroy:function(){this.element.removeClass("ui-buttonset");
this.buttons.map(function(){return E(this).button("widget")[0]
}).removeClass("ui-corner-left ui-corner-right").end().button("destroy")
}});
return E.ui.button
}));