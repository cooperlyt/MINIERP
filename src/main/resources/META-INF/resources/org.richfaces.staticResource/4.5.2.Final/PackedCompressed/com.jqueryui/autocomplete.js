/*
 * jQuery UI Autocomplete 1.11.2
 * http://jqueryui.com
 *
 * Copyright 2014 jQuery Foundation and other contributors
 * Released under the MIT license.
 * http://jquery.org/license
 *
 * http://api.jqueryui.com/autocomplete/
 */
(function(A){if(typeof define==="function"&&define.amd){define(["jquery","./core","./widget","./position","./menu"],A)
}else{A(jQuery)
}}(function(A){A.widget("ui.autocomplete",{version:"1.11.2",defaultElement:"<input>",options:{appendTo:null,autoFocus:false,delay:300,minLength:1,position:{my:"left top",at:"left bottom",collision:"none"},source:null,change:null,close:null,focus:null,open:null,response:null,search:null,select:null},requestIndex:0,pending:0,_create:function(){var D,B,E,G=this.element[0].nodeName.toLowerCase(),F=G==="textarea",C=G==="input";
this.isMultiLine=F?true:C?false:this.element.prop("isContentEditable");
this.valueMethod=this.element[F||C?"val":"text"];
this.isNewMenu=true;
this.element.addClass("ui-autocomplete-input").attr("autocomplete","off");
this._on(this.element,{keydown:function(H){if(this.element.prop("readOnly")){D=true;
E=true;
B=true;
return 
}D=false;
E=false;
B=false;
var I=A.ui.keyCode;
switch(H.keyCode){case I.PAGE_UP:D=true;
this._move("previousPage",H);
break;
case I.PAGE_DOWN:D=true;
this._move("nextPage",H);
break;
case I.UP:D=true;
this._keyEvent("previous",H);
break;
case I.DOWN:D=true;
this._keyEvent("next",H);
break;
case I.ENTER:if(this.menu.active){D=true;
H.preventDefault();
this.menu.select(H)
}break;
case I.TAB:if(this.menu.active){this.menu.select(H)
}break;
case I.ESCAPE:if(this.menu.element.is(":visible")){if(!this.isMultiLine){this._value(this.term)
}this.close(H);
H.preventDefault()
}break;
default:B=true;
this._searchTimeout(H);
break
}},keypress:function(H){if(D){D=false;
if(!this.isMultiLine||this.menu.element.is(":visible")){H.preventDefault()
}return 
}if(B){return 
}var I=A.ui.keyCode;
switch(H.keyCode){case I.PAGE_UP:this._move("previousPage",H);
break;
case I.PAGE_DOWN:this._move("nextPage",H);
break;
case I.UP:this._keyEvent("previous",H);
break;
case I.DOWN:this._keyEvent("next",H);
break
}},input:function(H){if(E){E=false;
H.preventDefault();
return 
}this._searchTimeout(H)
},focus:function(){this.selectedItem=null;
this.previous=this._value()
},blur:function(H){if(this.cancelBlur){delete this.cancelBlur;
return 
}clearTimeout(this.searching);
this.close(H);
this._change(H)
}});
this._initSource();
this.menu=A("<ul>").addClass("ui-autocomplete ui-front").appendTo(this._appendTo()).menu({role:null}).hide().menu("instance");
this._on(this.menu.element,{mousedown:function(H){H.preventDefault();
this.cancelBlur=true;
this._delay(function(){delete this.cancelBlur
});
var I=this.menu.element[0];
if(!A(H.target).closest(".ui-menu-item").length){this._delay(function(){var J=this;
this.document.one("mousedown",function(K){if(K.target!==J.element[0]&&K.target!==I&&!A.contains(I,K.target)){J.close()
}})
})
}},menufocus:function(J,K){var H,I;
if(this.isNewMenu){this.isNewMenu=false;
if(J.originalEvent&&/^mouse/.test(J.originalEvent.type)){this.menu.blur();
this.document.one("mousemove",function(){A(J.target).trigger(J.originalEvent)
});
return 
}}I=K.item.data("ui-autocomplete-item");
if(false!==this._trigger("focus",J,{item:I})){if(J.originalEvent&&/^key/.test(J.originalEvent.type)){this._value(I.value)
}}H=K.item.attr("aria-label")||I.value;
if(H&&A.trim(H).length){this.liveRegion.children().hide();
A("<div>").text(H).appendTo(this.liveRegion)
}},menuselect:function(J,K){var I=K.item.data("ui-autocomplete-item"),H=this.previous;
if(this.element[0]!==this.document[0].activeElement){this.element.focus();
this.previous=H;
this._delay(function(){this.previous=H;
this.selectedItem=I
})
}if(false!==this._trigger("select",J,{item:I})){this._value(I.value)
}this.term=this._value();
this.close(J);
this.selectedItem=I
}});
this.liveRegion=A("<span>",{role:"status","aria-live":"assertive","aria-relevant":"additions"}).addClass("ui-helper-hidden-accessible").appendTo(this.document[0].body);
this._on(this.window,{beforeunload:function(){this.element.removeAttr("autocomplete")
}})
},_destroy:function(){clearTimeout(this.searching);
this.element.removeClass("ui-autocomplete-input").removeAttr("autocomplete");
this.menu.element.remove();
this.liveRegion.remove()
},_setOption:function(B,C){this._super(B,C);
if(B==="source"){this._initSource()
}if(B==="appendTo"){this.menu.element.appendTo(this._appendTo())
}if(B==="disabled"&&C&&this.xhr){this.xhr.abort()
}},_appendTo:function(){var B=this.options.appendTo;
if(B){B=B.jquery||B.nodeType?A(B):this.document.find(B).eq(0)
}if(!B||!B[0]){B=this.element.closest(".ui-front")
}if(!B.length){B=this.document[0].body
}return B
},_initSource:function(){var D,B,C=this;
if(A.isArray(this.options.source)){D=this.options.source;
this.source=function(F,E){E(A.ui.autocomplete.filter(D,F.term))
}
}else{if(typeof this.options.source==="string"){B=this.options.source;
this.source=function(F,E){if(C.xhr){C.xhr.abort()
}C.xhr=A.ajax({url:B,data:F,dataType:"json",success:function(G){E(G)
},error:function(){E([])
}})
}
}else{this.source=this.options.source
}}},_searchTimeout:function(B){clearTimeout(this.searching);
this.searching=this._delay(function(){var D=this.term===this._value(),C=this.menu.element.is(":visible"),E=B.altKey||B.ctrlKey||B.metaKey||B.shiftKey;
if(!D||(D&&!C&&!E)){this.selectedItem=null;
this.search(null,B)
}},this.options.delay)
},search:function(C,B){C=C!=null?C:this._value();
this.term=this._value();
if(C.length<this.options.minLength){return this.close(B)
}if(this._trigger("search",B)===false){return 
}return this._search(C)
},_search:function(B){this.pending++;
this.element.addClass("ui-autocomplete-loading");
this.cancelSearch=false;
this.source({term:B},this._response())
},_response:function(){var B=++this.requestIndex;
return A.proxy(function(C){if(B===this.requestIndex){this.__response(C)
}this.pending--;
if(!this.pending){this.element.removeClass("ui-autocomplete-loading")
}},this)
},__response:function(B){if(B){B=this._normalize(B)
}this._trigger("response",null,{content:B});
if(!this.options.disabled&&B&&B.length&&!this.cancelSearch){this._suggest(B);
this._trigger("open")
}else{this._close()
}},close:function(B){this.cancelSearch=true;
this._close(B)
},_close:function(B){if(this.menu.element.is(":visible")){this.menu.element.hide();
this.menu.blur();
this.isNewMenu=true;
this._trigger("close",B)
}},_change:function(B){if(this.previous!==this._value()){this._trigger("change",B,{item:this.selectedItem})
}},_normalize:function(B){if(B.length&&B[0].label&&B[0].value){return B
}return A.map(B,function(C){if(typeof C==="string"){return{label:C,value:C}
}return A.extend({},C,{label:C.label||C.value,value:C.value||C.label})
})
},_suggest:function(B){var C=this.menu.element.empty();
this._renderMenu(C,B);
this.isNewMenu=true;
this.menu.refresh();
C.show();
this._resizeMenu();
C.position(A.extend({of:this.element},this.options.position));
if(this.options.autoFocus){this.menu.next()
}},_resizeMenu:function(){var B=this.menu.element;
B.outerWidth(Math.max(B.width("").outerWidth()+1,this.element.outerWidth()))
},_renderMenu:function(C,B){var D=this;
A.each(B,function(E,F){D._renderItemData(C,F)
})
},_renderItemData:function(B,C){return this._renderItem(B,C).data("ui-autocomplete-item",C)
},_renderItem:function(B,C){return A("<li>").text(C.label).appendTo(B)
},_move:function(C,B){if(!this.menu.element.is(":visible")){this.search(null,B);
return 
}if(this.menu.isFirstItem()&&/^previous/.test(C)||this.menu.isLastItem()&&/^next/.test(C)){if(!this.isMultiLine){this._value(this.term)
}this.menu.blur();
return 
}this.menu[C](B)
},widget:function(){return this.menu.element
},_value:function(){return this.valueMethod.apply(this.element,arguments)
},_keyEvent:function(C,B){if(!this.isMultiLine||this.menu.element.is(":visible")){this._move(C,B);
B.preventDefault()
}}});
A.extend(A.ui.autocomplete,{escapeRegex:function(B){return B.replace(/[\-\[\]{}()*+?.,\\\^$|#\s]/g,"\\$&")
},filter:function(D,B){var C=new RegExp(A.ui.autocomplete.escapeRegex(B),"i");
return A.grep(D,function(E){return C.test(E.label||E.value||E)
})
}});
A.widget("ui.autocomplete",A.ui.autocomplete,{options:{messages:{noResults:"No search results.",results:function(B){return B+(B>1?" results are":" result is")+" available, use up and down arrow keys to navigate."
}}},__response:function(C){var B;
this._superApply(arguments);
if(this.options.disabled||this.cancelSearch){return 
}if(C&&C.length){B=this.options.messages.results(C.length)
}else{B=this.options.messages.noResults
}this.liveRegion.children().hide();
A("<div>").text(B).appendTo(this.liveRegion)
}});
return A.ui.autocomplete
}));