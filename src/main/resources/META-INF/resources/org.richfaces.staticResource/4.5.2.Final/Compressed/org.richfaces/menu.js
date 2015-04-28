(function(C,B){B.ui=B.ui||{};
var A={positionType:"DROPDOWN",direction:"AA",jointPoint:"AA",cssRoot:"ddm",cssClasses:{}};
B.ui.Menu=function(G,F){this.options={};
C.extend(this.options,A,F||{});
C.extend(this.options.cssClasses,E.call(this,this.options.cssRoot));
D.constructor.call(this,G,this.options);
this.id=G;
this.namespace=this.namespace||"."+B.Event.createNamespace(this.name,this.id);
this.groupList=new Array();
this.target=this.getTarget();
this.targetComponent=B.component(this.target);
if(this.target){var H=this;
C(document).ready(function(){if(H.targetComponent&&H.targetComponent.contextMenuAttach){H.targetComponent.contextMenuAttach(H);
C("body").on("rich:ready"+H.namespace,'[id="'+H.target+'"]',function(){H.targetComponent.contextMenuAttach(H)
})
}else{B.Event.bindById(H.target,H.options.showEvent,C.proxy(H.__showHandler,H),H)
}})
}this.element=C(B.getDomElement(this.id));
if(!B.ui.MenuManager){B.ui.MenuManager={}
}this.menuManager=B.ui.MenuManager
};
var E=function(G){var F={selectMenuCss:"rf-"+G+"-sel",unselectMenuCss:"rf-"+G+"-unsel"};
return F
};
B.ui.MenuBase.extend(B.ui.Menu);
var D=B.ui.Menu.$super;
C.extend(B.ui.Menu.prototype,B.ui.MenuKeyNavigation);
C.extend(B.ui.Menu.prototype,(function(){return{name:"Menu",initiateGroups:function(F){for(var H in F){var G=F[H].id;
if(null!=G){this.groupList[G]=new B.ui.MenuGroup(G,{rootMenuId:this.id,onshow:F[H].onshow,onhide:F[H].onhide,horizontalOffset:F[H].horizontalOffset,verticalOffset:F[H].verticalOffset,jointPoint:F[H].jointPoint,direction:F[H].direction,cssRoot:F[H].cssRoot})
}}},getTarget:function(){return this.id+"_label"
},show:function(F){if(this.menuManager.openedMenu!=this.id){this.menuManager.shutdownMenu();
this.menuManager.addMenuId(this.id);
this.__showPopup()
}},hide:function(){this.__hidePopup();
this.menuManager.deletedMenuId()
},select:function(){this.element.removeClass(this.options.cssClasses.unselectMenuCss);
this.element.addClass(this.options.cssClasses.selectMenuCss)
},unselect:function(){this.element.removeClass(this.options.cssClasses.selectMenuCss);
this.element.addClass(this.options.cssClasses.unselectMenuCss)
},__overHandler:function(){D.__overHandler.call(this);
this.select()
},__leaveHandler:function(){D.__leaveHandler.call(this);
this.unselect()
},destroy:function(){this.detach(this.id);
if(this.target){B.Event.unbindById(this.target,this.options.showEvent);
if(this.targetComponent&&this.targetComponent.contextMenuAttach){C("body").off("rich:ready"+this.namespace,'[id="'+this.target+'"]')
}}D.destroy.call(this)
}}
})());
B.ui.MenuManager={openedMenu:null,activeSubMenu:null,addMenuId:function(F){this.openedMenu=F
},deletedMenuId:function(){this.openedMenu=null
},shutdownMenu:function(){if(this.openedMenu!=null){B.component(B.getDomElement(this.openedMenu)).hide()
}this.deletedMenuId()
},setActiveSubMenu:function(F){this.activeSubMenu=F
},getActiveSubMenu:function(){return this.activeSubMenu
}}
})(RichFaces.jQuery,RichFaces);