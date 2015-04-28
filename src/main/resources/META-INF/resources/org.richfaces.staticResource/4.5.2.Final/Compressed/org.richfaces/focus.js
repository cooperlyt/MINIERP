(function(E,C){C.ui=C.ui||{};
var B={useNative:false};
C.ui.Focus=C.BaseComponent.extendClass({name:"Focus",init:function(J,I){F.constructor.call(this,J);
I=this.options=E.extend({},B,I);
this.attachToDom(this.id);
var L=E(document.getElementById(J+"InputFocus"));
var K=this.options.focusCandidates;
E(document).on("focus",":tabbable",function(O){var N=E(O.target);
if(!N.is(":editable")){return 
}var M=O.target.id||"";
N.parents().each(function(){var P=E(this).attr("id");
if(P){M+=" "+P
}});
L.val(M);
C.log.debug("Focus - clientId candidates for components: "+M)
});
if(this.options.mode==="VIEW"){E(document).on("ajaxsubmit submit","form",function(O){var N=E(O.target);
var M=E("input[name='org.richfaces.focus']",N);
if(!M.length){M=E('<input name="org.richfaces.focus" type="hidden" />').appendTo(N)
}M.val(L.val())
})
}this.options.applyFocus=E.proxy(function(){var M=E();
if(K){var N=K;
C.log.debug("Focus - focus candidates: "+N);
N=N.split(" ");
E.each(N,function(P,O){var Q=E(document.getElementById(O));
M=M.add(E(":tabbable",Q));
if(Q.is(":tabbable")){M=M.add(Q)
}});
if(M.length==0){M=E("form").has(L).find(":tabbable")
}}else{if(this.options.mode=="VIEW"){M=E("body form:first :tabbable")
}}if(M.length>0){M=M.sort(D);
M.get(0).focus()
}},this)
},applyFocus:function(){E(this.options.applyFocus)
},destroy:function(){F.destroy.call(this)
}});
var D=function(K,J){var I=H(E(K).attr("tabindex"),E(J).attr("tabindex"));
return(I!=0)?I:G(K,J)
};
var H=function(J,I){if(J){if(I){return J-I
}else{return -1
}}else{if(I){return +1
}else{return 0
}}};
var G=function(J,I){var K=A(J,I);
if(J==I){return 0
}else{if(K.parent==J){return -1
}else{if(K.parent==I){return +1
}else{return E(K.aPrevious).index()-E(K.bPrevious).index()
}}}};
var A=function(J,I){var M=E(J).add(E(J).parents()).get().reverse();
var L=E(I).add(E(I).parents()).get().reverse();
var K={aPrevious:J,bPrevious:I};
E.each(M,function(O,N){E.each(L,function(P,Q){if(N==Q){K.parent=N;
return false
}K.bPrevious=Q
});
if(K.parent){return false
}K.aPrevious=N
});
if(!K.parent){return null
}return K
};
C.ui.Focus.__fn={sortTabindex:D,sortTabindexNums:H,searchCommonParent:A,sortByDOMOrder:G};
var F=C.ui.Focus.$super
})(RichFaces.jQuery,RichFaces);