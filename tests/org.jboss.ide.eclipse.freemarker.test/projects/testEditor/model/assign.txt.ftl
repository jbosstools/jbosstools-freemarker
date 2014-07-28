<#assign key="val">
<#assign seasons = ["winter", "spring", "summer", "autumn"]>
<#assign counter = counter + 1>
<#assign
  days = ["Mo", "Tu", "We", "Th", "Fr", "Sa", "Su"]
  counter = counter + 1
>
<#macro myMacro>foo</#macro>
<#assign formattedSeasons>
  <#list seasons as s>
    ${s} <@myMacro />
  </#list>
</#assign>
Number of words: ${formattedSeasons?word_list?size}
${formattedSeasons}
<#assign hello="Hello ${user}!">
${hello}