(function(C,B){B.ui=B.ui||{};
var A={showEvent:"contextmenu",cssRoot:"ctx",cssClasses:{},attached:true};
B.ui.ContextMenu=function(F,E){this.options={};
C.extend(this.options,A,E||{});
D.constructor.call(this,F,this.options);
this.id=F;
this.namespace=this.namespace||"."+B.Event.createNamespace(this.name,this.id);
B.Event.bind("body","click"+this.namespace,C.proxy(this.__leaveHandler,this));
B.Event.bindById(this.id,"click"+this.namespace,C.proxy(this.__clilckHandler,this))
};
B.ui.Menu.extend(B.ui.ContextMenu);
var D=B.ui.ContextMenu.$super;
C.extend(B.ui.ContextMenu.prototype,(function(){return{name:"ContextMenu",getTarget:function(){if(!this.options.attached){return null
}var E=typeof this.options.target==="undefined"?this.element.parentNode.id:this.options.target;
return E
},__showHandler:function(E){if(this.__isShown()){this.hide()
}return D.__showHandler.call(this,E)
},show:function(F){if(this.menuManager.openedMenu!=this.id){this.menuManager.shutdownMenu();
this.menuManager.addMenuId(this.id);
this.__showPopup(F);
var E=B.component(this.target);
if(E&&E.contextMenuShow){E.contextMenuShow(this,F)
}}},__clilckHandler:function(E){E.preventDefault();
E.stopPropagation()
},destroy:function(){B.Event.unbind("body","click"+this.namespace);
B.Event.unbindById(this.id,"click"+this.namespace);
D.destroy.call(this)
}}
})())
})(RichFaces.jQuery,RichFaces);