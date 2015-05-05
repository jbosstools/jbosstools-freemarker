<#assign attributes = {"href":"https://example.com","target":"_blank"}>
<h1>Welcome ${user}!</h1>
<p style="font-weight: bold">Our latest product:</p>
<a href="${url}">${content}<!-- comment 1 --></a>
<${tag} ${attribute}="${value}">${content}</${tag}>
<${tag} <#list attributes?keys as key>${key}="${attributes[key]}" </#list>>${content}</${tag}>!
<!-- <p style="font-weight:bold">Our latest product:</p> -->
<!-- ${content} -->
<#-- ${content} -->