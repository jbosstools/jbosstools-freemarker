<#macro m1>
  Macro 1
</#macro>
<#-- call the macro: -->
<@m1/>

<#function avg1 x y>
  <#return (x + y) / 2>
</#function>

<#macro m2 arg1 arg2>
  Macro 2: ${arg1} ${arg2}
</#macro>

<#function avg2 nums...>
  <#local sum = 0>
  <#list nums as num>
    <#local sum = sum + num>
  </#list>
  <#if nums?size != 0>
    <#return sum / nums?size>
  </#if>
</#function>


${avg1(10, 20)}
${avg2(10, 20, 30, 40)}
${avg2()!"N/A"}