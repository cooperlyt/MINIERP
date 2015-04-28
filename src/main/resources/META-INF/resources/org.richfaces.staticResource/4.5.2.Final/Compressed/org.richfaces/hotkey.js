(function(D,C){C.ui=C.ui||{};
var A={enabledInInput:false,preventDefault:true};
var B=["keydown","keyup"];
C.ui.HotKey=function(G,F){E.constructor.call(this,G);
this.namespace=this.namespace||"."+C.Event.createNamespace(this.name,this.id);
this.attachToDom(this.componentId);
this.options=D.extend({},A,F);
this.__handlers={};
this.options.selector=(this.options.selector)?this.options.selector:document;
D(document).ready(D.proxy(function(){this.__bindDefinedHandlers()
},this))
};
C.BaseComponent.extend(C.ui.HotKey);
var E=C.ui.HotKey.$super;
D.extend(C.ui.HotKey.prototype,{name:"HotKey",__bindDefinedHandlers:function(){for(var F=0;
F<B.length;
F++){if(this.options["on"+B[F]]){this.__bindHandler(B[F])
}}},__bindHandler:function(F){this.__handlers[F]=D.proxy(function(H){var G=this.invokeEvent.call(this,F,document.getElementById(this.id),H);
if(this.options.preventDefault){H.stopPropagation();
H.preventDefault();
return false
}return G
},this);
D(this.options.selector).bind(F+this.namespace,this.options,this.__handlers[F])
},destroy:function(){C.Event.unbindById(this.id,this.namespace);
for(var F in this.__handlers){if(this.__handlers.hasOwnProperty(F)){D(this.options.selector).unbind(F+this.namespace,this.__handlers[F])
}}E.destroy.call(this)
}})
})(RichFaces.jQuery,RichFaces);