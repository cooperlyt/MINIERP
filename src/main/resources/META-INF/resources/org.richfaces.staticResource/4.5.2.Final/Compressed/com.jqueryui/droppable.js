/*
 * jQuery UI Droppable 1.11.2
 * http://jqueryui.com
 *
 * Copyright 2014 jQuery Foundation and other contributors
 * Released under the MIT license.
 * http://jquery.org/license
 *
 * http://api.jqueryui.com/droppable/
 */
(function(A){if(typeof define==="function"&&define.amd){define(["jquery","./core","./widget","./mouse","./draggable"],A)
}else{A(jQuery)
}}(function(A){A.widget("ui.droppable",{version:"1.11.2",widgetEventPrefix:"drop",options:{accept:"*",activeClass:false,addClasses:true,greedy:false,hoverClass:false,scope:"default",tolerance:"intersect",activate:null,deactivate:null,drop:null,out:null,over:null},_create:function(){var C,D=this.options,B=D.accept;
this.isover=false;
this.isout=true;
this.accept=A.isFunction(B)?B:function(E){return E.is(B)
};
this.proportions=function(){if(arguments.length){C=arguments[0]
}else{return C?C:C={width:this.element[0].offsetWidth,height:this.element[0].offsetHeight}
}};
this._addToManager(D.scope);
D.addClasses&&this.element.addClass("ui-droppable")
},_addToManager:function(B){A.ui.ddmanager.droppables[B]=A.ui.ddmanager.droppables[B]||[];
A.ui.ddmanager.droppables[B].push(this)
},_splice:function(B){var C=0;
for(;
C<B.length;
C++){if(B[C]===this){B.splice(C,1)
}}},_destroy:function(){var B=A.ui.ddmanager.droppables[this.options.scope];
this._splice(B);
this.element.removeClass("ui-droppable ui-droppable-disabled")
},_setOption:function(C,D){if(C==="accept"){this.accept=A.isFunction(D)?D:function(E){return E.is(D)
}
}else{if(C==="scope"){var B=A.ui.ddmanager.droppables[this.options.scope];
this._splice(B);
this._addToManager(D)
}}this._super(C,D)
},_activate:function(C){var B=A.ui.ddmanager.current;
if(this.options.activeClass){this.element.addClass(this.options.activeClass)
}if(B){this._trigger("activate",C,this.ui(B))
}},_deactivate:function(C){var B=A.ui.ddmanager.current;
if(this.options.activeClass){this.element.removeClass(this.options.activeClass)
}if(B){this._trigger("deactivate",C,this.ui(B))
}},_over:function(C){var B=A.ui.ddmanager.current;
if(!B||(B.currentItem||B.element)[0]===this.element[0]){return 
}if(this.accept.call(this.element[0],(B.currentItem||B.element))){if(this.options.hoverClass){this.element.addClass(this.options.hoverClass)
}this._trigger("over",C,this.ui(B))
}},_out:function(C){var B=A.ui.ddmanager.current;
if(!B||(B.currentItem||B.element)[0]===this.element[0]){return 
}if(this.accept.call(this.element[0],(B.currentItem||B.element))){if(this.options.hoverClass){this.element.removeClass(this.options.hoverClass)
}this._trigger("out",C,this.ui(B))
}},_drop:function(C,D){var B=D||A.ui.ddmanager.current,E=false;
if(!B||(B.currentItem||B.element)[0]===this.element[0]){return false
}this.element.find(":data(ui-droppable)").not(".ui-draggable-dragging").each(function(){var F=A(this).droppable("instance");
if(F.options.greedy&&!F.options.disabled&&F.options.scope===B.options.scope&&F.accept.call(F.element[0],(B.currentItem||B.element))&&A.ui.intersect(B,A.extend(F,{offset:F.element.offset()}),F.options.tolerance,C)){E=true;
return false
}});
if(E){return false
}if(this.accept.call(this.element[0],(B.currentItem||B.element))){if(this.options.activeClass){this.element.removeClass(this.options.activeClass)
}if(this.options.hoverClass){this.element.removeClass(this.options.hoverClass)
}this._trigger("drop",C,this.ui(B));
return this.element
}return false
},ui:function(B){return{draggable:(B.currentItem||B.element),helper:B.helper,position:B.position,offset:B.positionAbs}
}});
A.ui.intersect=(function(){function B(D,C,E){return(D>=C)&&(D<(C+E))
}return function(N,H,L,D){if(!H.offset){return false
}var F=(N.positionAbs||N.position.absolute).left+N.margins.left,K=(N.positionAbs||N.position.absolute).top+N.margins.top,E=F+N.helperProportions.width,J=K+N.helperProportions.height,G=H.offset.left,M=H.offset.top,C=G+H.proportions().width,I=M+H.proportions().height;
switch(L){case"fit":return(G<=F&&E<=C&&M<=K&&J<=I);
case"intersect":return(G<F+(N.helperProportions.width/2)&&E-(N.helperProportions.width/2)<C&&M<K+(N.helperProportions.height/2)&&J-(N.helperProportions.height/2)<I);
case"pointer":return B(D.pageY,M,H.proportions().height)&&B(D.pageX,G,H.proportions().width);
case"touch":return((K>=M&&K<=I)||(J>=M&&J<=I)||(K<M&&J>I))&&((F>=G&&F<=C)||(E>=G&&E<=C)||(F<G&&E>C));
default:return false
}}
})();
A.ui.ddmanager={current:null,droppables:{"default":[]},prepareOffsets:function(E,G){var D,C,B=A.ui.ddmanager.droppables[E.options.scope]||[],F=G?G.type:null,H=(E.currentItem||E.element).find(":data(ui-droppable)").addBack();
droppablesLoop:for(D=0;
D<B.length;
D++){if(B[D].options.disabled||(E&&!B[D].accept.call(B[D].element[0],(E.currentItem||E.element)))){continue
}for(C=0;
C<H.length;
C++){if(H[C]===B[D].element[0]){B[D].proportions().height=0;
continue droppablesLoop
}}B[D].visible=B[D].element.css("display")!=="none";
if(!B[D].visible){continue
}if(F==="mousedown"){B[D]._activate.call(B[D],G)
}B[D].offset=B[D].element.offset();
B[D].proportions({width:B[D].element[0].offsetWidth,height:B[D].element[0].offsetHeight})
}},drop:function(B,C){var D=false;
A.each((A.ui.ddmanager.droppables[B.options.scope]||[]).slice(),function(){if(!this.options){return 
}if(!this.options.disabled&&this.visible&&A.ui.intersect(B,this,this.options.tolerance,C)){D=this._drop.call(this,C)||D
}if(!this.options.disabled&&this.visible&&this.accept.call(this.element[0],(B.currentItem||B.element))){this.isout=true;
this.isover=false;
this._deactivate.call(this,C)
}});
return D
},dragStart:function(B,C){B.element.parentsUntil("body").bind("scroll.droppable",function(){if(!B.options.refreshPositions){A.ui.ddmanager.prepareOffsets(B,C)
}})
},drag:function(B,C){if(B.options.refreshPositions){A.ui.ddmanager.prepareOffsets(B,C)
}A.each(A.ui.ddmanager.droppables[B.options.scope]||[],function(){if(this.options.disabled||this.greedyChild||!this.visible){return 
}var G,E,D,F=A.ui.intersect(B,this,this.options.tolerance,C),H=!F&&this.isover?"isout":(F&&!this.isover?"isover":null);
if(!H){return 
}if(this.options.greedy){E=this.options.scope;
D=this.element.parents(":data(ui-droppable)").filter(function(){return A(this).droppable("instance").options.scope===E
});
if(D.length){G=A(D[0]).droppable("instance");
G.greedyChild=(H==="isover")
}}if(G&&H==="isover"){G.isover=false;
G.isout=true;
G._out.call(G,C)
}this[H]=true;
this[H==="isout"?"isover":"isout"]=false;
this[H==="isover"?"_over":"_out"].call(this,C);
if(G&&H==="isout"){G.isout=false;
G.isover=true;
G._over.call(G,C)
}})
},dragStop:function(B,C){B.element.parentsUntil("body").unbind("scroll.droppable");
if(!B.options.refreshPositions){A.ui.ddmanager.prepareOffsets(B,C)
}}};
return A.ui.droppable
}));