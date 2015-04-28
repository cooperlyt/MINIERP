(function($){var undefined,dataFlag="watermark",dataClass="watermarkClass",dataFocus="watermarkFocus",dataFormSubmit="watermarkSubmit",dataMaxLen="watermarkMaxLength",dataPassword="watermarkPassword",dataText="watermarkText",selWatermarkDefined=":data("+dataFlag+")",selWatermarkAble=":text,:password,:search,textarea",triggerFns=["Page_ClientValidate"],pageDirty=false;
$.extend($.expr[":"],{search:function(elem){return"search"===(elem.type||"")
},data:function(element,index,matches,set){var data,parts=/^((?:[^=!^$*]|[!^$*](?!=))+)(?:([!^$*]?=)(.*))?$/.exec(matches[3]);
if(parts){data=$(element).data(parts[1]);
if(data!==undefined){if(parts[2]){data=""+data;
switch(parts[2]){case"=":return(data==parts[3]);
case"!=":return(data!=parts[3]);
case"^=":return(data.slice(0,parts[3].length)==parts[3]);
case"$=":return(data.slice(-parts[3].length)==parts[3]);
case"*=":return(data.indexOf(parts[3])!==-1)
}}return true
}}return false
}});
$.watermark={version:"3.0.6",options:{className:"watermark",useNative:true},hide:function(selector){$(selector).filter(selWatermarkDefined).each(function(){$.watermark._hide($(this))
})
},_hide:function($input,focus){var inputVal=$input.val()||"",inputWm=$input.data(dataText)||"",maxLen=$input.data(dataMaxLen)||0,className=$input.data(dataClass);
if((inputWm.length)&&(inputVal==inputWm)){$input.val("");
if($input.data(dataPassword)){if(($input.attr("type")||"")==="text"){var $pwd=$input.data(dataPassword)||[],$wrap=$input.parent()||[];
if(($pwd.length)&&($wrap.length)){$wrap[0].removeChild($input[0]);
$wrap[0].appendChild($pwd[0]);
$input=$pwd
}}}if(maxLen){$input.attr("maxLength",maxLen);
$input.removeData(dataMaxLen)
}if(focus){$input.attr("autocomplete","off");
window.setTimeout(function(){$input.select()
},1)
}}className&&$input.removeClass(className)
},show:function(selector){$(selector).filter(selWatermarkDefined).each(function(){$.watermark._show($(this))
})
},_show:function($input){var val=$input.val()||"",text=$input.data(dataText)||"",type=$input.attr("type")||"",className=$input.data(dataClass);
if(((val.length==0)||(val==text))&&(!$input.data(dataFocus))){pageDirty=true;
if($input.data(dataPassword)){if(type==="password"){var $pwd=$input.data(dataPassword)||[],$wrap=$input.parent()||[];
if(($pwd.length)&&($wrap.length)){$wrap[0].removeChild($input[0]);
$wrap[0].appendChild($pwd[0]);
$input=$pwd;
$input.attr("maxLength",text.length)
}}}if((type==="text")||(type==="search")){var maxLen=$input.attr("maxLength")||0;
if((maxLen>0)&&(text.length>maxLen)){$input.data(dataMaxLen,maxLen);
$input.attr("maxLength",text.length)
}}className&&$input.addClass(className);
$input.val(text)
}else{$.watermark._hide($input)
}},hideAll:function(){if(pageDirty){$.watermark.hide(selWatermarkAble);
pageDirty=false
}},showAll:function(){$.watermark.show(selWatermarkAble)
}};
$.fn.watermark=function(text,options){if(!this.length){return this
}var hasClass=false,hasText=(typeof (text)==="string");
if(typeof (options)==="object"){hasClass=(typeof (options.className)==="string");
options=$.extend({},$.watermark.options,options)
}else{if(typeof (options)==="string"){hasClass=true;
options=$.extend({},$.watermark.options,{className:options})
}else{options=$.watermark.options
}}if(typeof (options.useNative)!=="function"){options.useNative=options.useNative?function(){return true
}:function(){return false
}
}return this.each(function(){var $input=$(this);
if(!$input.is(selWatermarkAble)){return 
}if($input.data(dataFlag)){if(hasText||hasClass){$.watermark._hide($input);
if(hasText){$input.data(dataText,text)
}if(hasClass){$input.data(dataClass,options.className)
}}}else{if(options.useNative.call(this,$input)){if(((""+$input.css("-webkit-appearance")).replace("undefined","")!=="")&&((($input.attr("tagName")||"")!=="TEXTAREA"))&&$input.size()>0&&$input[0].tagName!=="TEXTAREA"){if(hasText){$input.attr("placeholder",text)
}return 
}}$input.data(dataText,hasText?text:"");
$input.data(dataClass,options.className);
$input.data(dataFlag,1);
if(($input.attr("type")||"")==="password"){var $wrap=$input.wrap("<span>").parent(),$wm=$($wrap.html().replace(/type=["']?password["']?/i,'type="text"'));
$wm.data(dataText,$input.data(dataText));
$wm.data(dataClass,$input.data(dataClass));
$wm.data(dataFlag,1);
$wm.attr("maxLength",text.length);
$wm.focus(function(){$.watermark._hide($wm,true)
}).bind("dragenter",function(){$.watermark._hide($wm)
}).bind("dragend",function(){window.setTimeout(function(){$wm.blur()
},1)
});
$input.blur(function(){$.watermark._show($input)
}).bind("dragleave",function(){$.watermark._show($input)
});
$wm.data(dataPassword,$input);
$input.data(dataPassword,$wm)
}else{$input.focus(function(){$input.data(dataFocus,1);
$.watermark._hide($input,true)
}).blur(function(){$input.data(dataFocus,0);
$.watermark._show($input)
}).bind("dragenter",function(){$.watermark._hide($input)
}).bind("dragleave",function(){$.watermark._show($input)
}).bind("dragend",function(){window.setTimeout(function(){$.watermark._show($input)
},1)
}).bind("drop",function(evt){var dropText=evt.originalEvent.dataTransfer.getData("Text");
if($input.val().replace(dropText,"")===$input.data(dataText)){$input.val(dropText)
}$input.focus()
})
}if(this.form){var form=this.form,$form=$(form);
if(!$form.data(dataFormSubmit)){$form.submit($.watermark.hideAll);
if(form.submit){$form.data(dataFormSubmit,form.onsubmit||1);
form.onsubmit=(function(f,$f){return function(){var nativeSubmit=$f.data(dataFormSubmit);
$.watermark.hideAll();
if(nativeSubmit instanceof Function){nativeSubmit()
}else{eval(nativeSubmit)
}}
})(form,$form)
}else{$form.data(dataFormSubmit,1);
form.submit=(function(f){return function(){$.watermark.hideAll();
delete f.submit;
f.submit()
}
})(form)
}}}}$.watermark._show($input)
})
};
if(triggerFns.length){$(function(){var i,name,fn;
for(i=triggerFns.length-1;
i>=0;
i--){name=triggerFns[i];
fn=window[name];
if(typeof (fn)==="function"){window[name]=(function(origFn){return function(){$.watermark.hideAll();
return origFn.apply(null,Array.prototype.slice.call(arguments))
}
})(fn)
}}})
}})(jQuery);