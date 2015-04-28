/*
 * jQuery UI Widget 1.11.2
 * http://jqueryui.com
 *
 * Copyright 2014 jQuery Foundation and other contributors
 * Released under the MIT license.
 * http://jquery.org/license
 *
 * http://api.jqueryui.com/jQuery.widget/
 */
(function(A){if(typeof define==="function"&&define.amd){define(["jquery"],A)
}else{A(jQuery)
}}(function(B){var C=0,A=Array.prototype.slice;
B.cleanData=(function(D){return function(E){var G,H,F;
for(F=0;
(H=E[F])!=null;
F++){try{G=B._data(H,"events");
if(G&&G.remove){B(H).triggerHandler("remove")
}}catch(I){}}D(E)
}
})(B.cleanData);
B.widget=function(D,E,L){var I,J,G,K,F={},H=D.split(".")[0];
D=D.split(".")[1];
I=H+"-"+D;
if(!L){L=E;
E=B.Widget
}B.expr[":"][I.toLowerCase()]=function(M){return !!B.data(M,I)
};
B[H]=B[H]||{};
J=B[H][D];
G=B[H][D]=function(M,N){if(!this._createWidget){return new G(M,N)
}if(arguments.length){this._createWidget(M,N)
}};
B.extend(G,J,{version:L.version,_proto:B.extend({},L),_childConstructors:[]});
K=new E();
K.options=B.widget.extend({},K.options);
B.each(L,function(N,M){if(!B.isFunction(M)){F[N]=M;
return 
}F[N]=(function(){var O=function(){return E.prototype[N].apply(this,arguments)
},P=function(Q){return E.prototype[N].apply(this,Q)
};
return function(){var S=this._super,Q=this._superApply,R;
this._super=O;
this._superApply=P;
R=M.apply(this,arguments);
this._super=S;
this._superApply=Q;
return R
}
})()
});
G.prototype=B.widget.extend(K,{widgetEventPrefix:J?(K.widgetEventPrefix||D):D},F,{constructor:G,namespace:H,widgetName:D,widgetFullName:I});
if(J){B.each(J._childConstructors,function(N,O){var M=O.prototype;
B.widget(M.namespace+"."+M.widgetName,G,O._proto)
});
delete J._childConstructors
}else{E._childConstructors.push(G)
}B.widget.bridge(D,G);
return G
};
B.widget.extend=function(I){var E=A.call(arguments,1),H=0,D=E.length,F,G;
for(;
H<D;
H++){for(F in E[H]){G=E[H][F];
if(E[H].hasOwnProperty(F)&&G!==undefined){if(B.isPlainObject(G)){I[F]=B.isPlainObject(I[F])?B.widget.extend({},I[F],G):B.widget.extend({},G)
}else{I[F]=G
}}}}return I
};
B.widget.bridge=function(E,D){var F=D.prototype.widgetFullName||E;
B.fn[E]=function(I){var G=typeof I==="string",H=A.call(arguments,1),J=this;
I=!G&&H.length?B.widget.extend.apply(null,[I].concat(H)):I;
if(G){this.each(function(){var L,K=B.data(this,F);
if(I==="instance"){J=K;
return false
}if(!K){return B.error("cannot call methods on "+E+" prior to initialization; attempted to call method '"+I+"'")
}if(!B.isFunction(K[I])||I.charAt(0)==="_"){return B.error("no such method '"+I+"' for "+E+" widget instance")
}L=K[I].apply(K,H);
if(L!==K&&L!==undefined){J=L&&L.jquery?J.pushStack(L.get()):L;
return false
}})
}else{this.each(function(){var K=B.data(this,F);
if(K){K.option(I||{});
if(K._init){K._init()
}}else{B.data(this,F,new D(I,this))
}})
}return J
}
};
B.Widget=function(){};
B.Widget._childConstructors=[];
B.Widget.prototype={widgetName:"widget",widgetEventPrefix:"",defaultElement:"<div>",options:{disabled:false,create:null},_createWidget:function(D,E){E=B(E||this.defaultElement||this)[0];
this.element=B(E);
this.uuid=C++;
this.eventNamespace="."+this.widgetName+this.uuid;
this.bindings=B();
this.hoverable=B();
this.focusable=B();
if(E!==this){B.data(E,this.widgetFullName,this);
this._on(true,this.element,{remove:function(F){if(F.target===E){this.destroy()
}}});
this.document=B(E.style?E.ownerDocument:E.document||E);
this.window=B(this.document[0].defaultView||this.document[0].parentWindow)
}this.options=B.widget.extend({},this.options,this._getCreateOptions(),D);
this._create();
this._trigger("create",null,this._getCreateEventData());
this._init()
},_getCreateOptions:B.noop,_getCreateEventData:B.noop,_create:B.noop,_init:B.noop,destroy:function(){this._destroy();
this.element.unbind(this.eventNamespace).removeData(this.widgetFullName).removeData(B.camelCase(this.widgetFullName));
this.widget().unbind(this.eventNamespace).removeAttr("aria-disabled").removeClass(this.widgetFullName+"-disabled ui-state-disabled");
this.bindings.unbind(this.eventNamespace);
this.hoverable.removeClass("ui-state-hover");
this.focusable.removeClass("ui-state-focus")
},_destroy:B.noop,widget:function(){return this.element
},option:function(G,H){var D=G,I,F,E;
if(arguments.length===0){return B.widget.extend({},this.options)
}if(typeof G==="string"){D={};
I=G.split(".");
G=I.shift();
if(I.length){F=D[G]=B.widget.extend({},this.options[G]);
for(E=0;
E<I.length-1;
E++){F[I[E]]=F[I[E]]||{};
F=F[I[E]]
}G=I.pop();
if(arguments.length===1){return F[G]===undefined?null:F[G]
}F[G]=H
}else{if(arguments.length===1){return this.options[G]===undefined?null:this.options[G]
}D[G]=H
}}this._setOptions(D);
return this
},_setOptions:function(D){var E;
for(E in D){this._setOption(E,D[E])
}return this
},_setOption:function(D,E){this.options[D]=E;
if(D==="disabled"){this.widget().toggleClass(this.widgetFullName+"-disabled",!!E);
if(E){this.hoverable.removeClass("ui-state-hover");
this.focusable.removeClass("ui-state-focus")
}}return this
},enable:function(){return this._setOptions({disabled:false})
},disable:function(){return this._setOptions({disabled:true})
},_on:function(G,F,E){var H,D=this;
if(typeof G!=="boolean"){E=F;
F=G;
G=false
}if(!E){E=F;
F=this.element;
H=this.widget()
}else{F=H=B(F);
this.bindings=this.bindings.add(F)
}B.each(E,function(N,M){function K(){if(!G&&(D.options.disabled===true||B(this).hasClass("ui-state-disabled"))){return 
}return(typeof M==="string"?D[M]:M).apply(D,arguments)
}if(typeof M!=="string"){K.guid=M.guid=M.guid||K.guid||B.guid++
}var L=N.match(/^([\w:-]*)\s*(.*)$/),J=L[1]+D.eventNamespace,I=L[2];
if(I){H.delegate(I,J,K)
}else{F.bind(J,K)
}})
},_off:function(E,D){D=(D||"").split(" ").join(this.eventNamespace+" ")+this.eventNamespace;
E.unbind(D).undelegate(D);
this.bindings=B(this.bindings.not(E).get());
this.focusable=B(this.focusable.not(E).get());
this.hoverable=B(this.hoverable.not(E).get())
},_delay:function(G,F){function E(){return(typeof G==="string"?D[G]:G).apply(D,arguments)
}var D=this;
return setTimeout(E,F||0)
},_hoverable:function(D){this.hoverable=this.hoverable.add(D);
this._on(D,{mouseenter:function(E){B(E.currentTarget).addClass("ui-state-hover")
},mouseleave:function(E){B(E.currentTarget).removeClass("ui-state-hover")
}})
},_focusable:function(D){this.focusable=this.focusable.add(D);
this._on(D,{focusin:function(E){B(E.currentTarget).addClass("ui-state-focus")
},focusout:function(E){B(E.currentTarget).removeClass("ui-state-focus")
}})
},_trigger:function(D,E,F){var I,H,G=this.options[D];
F=F||{};
E=B.Event(E);
E.type=(D===this.widgetEventPrefix?D:this.widgetEventPrefix+D).toLowerCase();
E.target=this.element[0];
H=E.originalEvent;
if(H){for(I in H){if(!(I in E)){E[I]=H[I]
}}}this.element.trigger(E,F);
return !(B.isFunction(G)&&G.apply(this.element[0],[E].concat(F))===false||E.isDefaultPrevented())
}};
B.each({show:"fadeIn",hide:"fadeOut"},function(E,D){B.Widget.prototype["_"+E]=function(H,G,J){if(typeof G==="string"){G={effect:G}
}var I,F=!G?E:G===true||typeof G==="number"?D:G.effect||D;
G=G||{};
if(typeof G==="number"){G={duration:G}
}I=!B.isEmptyObject(G);
G.complete=J;
if(G.delay){H.delay(G.delay)
}if(I&&B.effects&&B.effects.effect[F]){H[E](G)
}else{if(F!==E&&H[F]){H[F](G.duration,G.easing,J)
}else{H.queue(function(K){B(this)[E]();
if(J){J.call(H[0])
}K()
})
}}}
});
return B.widget
}));