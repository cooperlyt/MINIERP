(function(C,B){B.ui=B.ui||{};
B.ui.Tab=B.ui.TogglePanelItem.extendClass({name:"Tab",init:function(F,E){D.constructor.call(this,F,E);
this.attachToDom();
this.index=E.index;
this.getTogglePanel().getItems()[this.index]=this
},__header:function(F){var G=C(B.getDomElement(this.id+":header"));
for(var E in A){if(E!==F){G.removeClass(A[E])
}if(!G.hasClass(A[F])){G.addClass(A[F])
}}return G
},__content:function(){if(!this.__content_){this.__content_=C(B.getDomElement(this.id))
}return this.__content_
},__enter:function(){this.__content().show();
this.__header("active");
return this.__fireEnter()
},__fireLeave:function(){return B.Event.fireById(this.id+":content","leave")
},__fireEnter:function(){return B.Event.fireById(this.id+":content","enter")
},__addUserEventHandler:function(F){var G=this.options["on"+F];
if(G){var E=B.Event.bindById(this.id+":content",F,G)
}},getHeight:function(E){if(E||!this.__height){this.__height=C(B.getDomElement(this.id)).outerHeight(true)
}return this.__height
},__leave:function(){var E=this.__fireLeave();
if(!E){return false
}this.__content().hide();
this.__header("inactive");
return true
},destroy:function(){var E=this.getTogglePanel();
if(E&&E.getItems&&E.getItems()[this.index]){delete E.getItems()[this.index]
}B.Event.unbindById(this.id);
D.destroy.call(this)
}});
var D=B.ui.Tab.$super;
var A={active:"rf-tab-hdr-act",inactive:"rf-tab-hdr-inact",disabled:"rf-tab-hdr-dis"}
})(RichFaces.jQuery,RichFaces);