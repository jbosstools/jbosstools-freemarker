<#assign seq = ["winter", "spring", "summer", "autumn"]>
<#list seq as x>
  ${x_index + 1}. ${x}<#if x_has_next>,</#if>
</#list> 

<#list 1..3 as n>
  <#list 1..3 as m>
    list item #${n}x${m}
  </#list>
</#list>

<#list seq as x>
  ${x}
  <#if x = "spring"><#break></#if>
</#list>

<#assign x>
  <#list 1..3 as n>
    list item #${n}
  </#list>
</#assign>
Number of words: ${x?word_list?size}
${x}

2.3.23 listing directives:
<#list 1..3>
  Items:
  <#items as n>
  	${n}<#sep>,
  </#items>
<#else>
  No items 
</#list>

<#list 1..3 as x>${x}<#sep>, </#list>

<#list 1..3 as x>
  <div>${x}<#sep>,</#sep></div>
</#list>