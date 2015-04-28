(function(C,B){B.ui=B.ui||{};
var A={useNative:false};
B.ui.Placeholder=B.BaseComponent.extendClass({name:"Placeholder",init:function(F,E){D.constructor.call(this,F);
E=C.extend({},A,E);
this.attachToDom(this.id);
C(function(){E.className="rf-plhdr "+((E.styleClass)?E.styleClass:"");
var H=(E.selector)?C(E.selector):C(document.getElementById(E.targetId));
var G=H.find("*").andSelf().filter(":editable");
G.watermark(E.text,E)
})
},destroy:function(){D.destroy.call(this)
}});
C(function(){C(document).on("ajaxsubmit","form",C.watermark.hideAll);
C(document).on("ajaxbegin","form",C.watermark.showAll);
C(document).on("reset","form",function(){setTimeout(C.watermark.showAll,0)
})
});
var D=B.ui.Placeholder.$super
})(RichFaces.jQuery,RichFaces);