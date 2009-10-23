<!DOCTYPE composition PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<#include "../util/TypeInfo.ftl">
<#assign entityName = pojo.shortName>
<#assign componentName = entityName?uncap_first>
<#assign homeName = componentName + "Home">
<#assign masterPageName = entityName + "List">
<#assign pageName = entityName>
<ui:composition xmlns="http://www.w3.org/1999/xhtml"
    xmlns:s="http://jboss.com/products/seam/taglib"
    xmlns:ui="http://java.sun.com/jsf/facelets"
    xmlns:f="http://java.sun.com/jsf/core"
    xmlns:h="http://java.sun.com/jsf/html"
    xmlns:a="http://richfaces.org/a4j"
    xmlns:rich="http://richfaces.org/rich"
    template="layout/template.xhtml">

<ui:define name="body">

    <h:form id="${componentName}" styleClass="edit">

        <rich:panel>
            <f:facet name="header">${'#'}{${homeName}.managed ? 'Edit' : 'Add'} ${label(entityName)}</f:facet>
<#foreach property in pojo.allPropertiesIterator>
<#include "editproperty.xhtml.ftl">
</#foreach>

            <div style="clear:both">
                <span class="required">*</span>
                required fields
            </div>

        </rich:panel>

        <div class="actionButtons">

            <h:commandButton id="save"
                          value="Save"
                         action="${'#'}{${homeName}.persist}"
                       disabled="${'#'}{!${homeName}.wired}"
                       rendered="${'#'}{!${homeName}.managed}"/>

            <h:commandButton id="update"
                          value="Save"
                         action="${'#'}{${homeName}.update}"
                       rendered="${'#'}{${homeName}.managed}"/>

            <h:commandButton id="delete"
                          value="Delete"
                         action="${'#'}{${homeName}.remove}"
                      immediate="true"
                       rendered="${'#'}{${homeName}.managed}"/>

            <s:button id="cancelEdit"
                   value="Cancel"
             propagation="end"
                    view="/${pageName}.xhtml"
                rendered="${'#'}{${homeName}.managed}"/>

            <s:button id="cancelAdd"
                   value="Cancel"
             propagation="end"
                    view="/${'#'}{empty ${componentName}From ? '${masterPageName}' : ${componentName}From}.xhtml"
                rendered="${'#'}{!${homeName}.managed}"/>

        </div>
    </h:form>
<#assign hasAssociations=false>
<#foreach property in pojo.allPropertiesIterator>
<#if isToOne(property) || c2h.isOneToManyCollection(property)>
<#assign hasAssociations=true>
</#if>
</#foreach>

<#if hasAssociations>
<rich:tabPanel switchType="ajax">
</#if>
<#foreach property in pojo.allPropertiesIterator>
<#if isToOne(property)>
<#assign parentPojo = c2j.getPOJOClass(cfg.getClassMapping(property.value.referencedEntityName))>
<#assign parentPageName = parentPojo.shortName>
<#assign parentName = parentPojo.shortName?uncap_first>

<#if property.optional>
    <rich:tab label="${label(property.name)}">
<#else>
    <rich:tab label="${label(property.name)} *" labelClass="required">
</#if>
    <div class="association" id="${property.name}Parent">

        <h:outputText value="There is no ${property.name} associated with this ${componentName}."
                   rendered="${'#'}{${homeName}.instance.${property.name} == null}"/>

        <rich:dataTable var="_${parentName}"
                   value="${'#'}{${homeName}.instance.${property.name}}"
                rendered="${'#'}{${homeName}.instance.${property.name} != null}"
              rowClasses="rvgRowOne,rvgRowTwo"
                      id="${property.name}Table">
<#foreach parentProperty in parentPojo.allPropertiesIterator>
<#if !c2h.isCollection(parentProperty) && !isToOne(parentProperty) && parentProperty != parentPojo.versionProperty!>
<#if parentPojo.isComponent(parentProperty)>
<#foreach componentProperty in parentProperty.value.propertyIterator>
            <h:column>
                <f:facet name="header">${label(componentProperty.name)}</f:facet>
                <@outputValue property=componentProperty expression="${'#'}{_${parentName}.${parentProperty.name}.${componentProperty.name}}" indent=16/>
            </h:column>
</#foreach>
<#else>
            <h:column>
                <f:facet name="header">${label(parentProperty.name)}</f:facet>
                <@outputValue property=parentProperty expression="${'#'}{_${parentName}.${parentProperty.name}}" indent=16/>
            </h:column>
</#if>
</#if>
<#if isToOne(parentProperty)>
<#assign grandparentPojo = c2j.getPOJOClass(cfg.getClassMapping(parentProperty.value.referencedEntityName))>
<#if grandparentPojo.isComponent(grandparentPojo.identifierProperty)>
<#foreach componentProperty in grandparentPojo.identifierProperty.value.propertyIterator>
            <h:column>
                <f:facet name="header">${label(parentProperty.name)} ${label(componentProperty.name)?uncap_first}</f:facet>
                <@outputValue property=componentProperty expression="${'#'}{_${parentName}.${parentProperty.name}.${grandparentPojo.identifierProperty.name}.${componentProperty.name}}" indent=16/>
            </h:column>
</#foreach>
<#else>
            <h:column>
                <f:facet name="header">${label(parentProperty.name)} ${label(grandparentPojo.identifierProperty.name)?uncap_first}</f:facet>
                <@outputValue property=grandparentPojo.identifierProperty expression="${'#'}{_${parentName}.${parentProperty.name}.${grandparentPojo.identifierProperty.name}}" indent=16/>
            </h:column>
</#if>
</#if>
</#foreach>
        </rich:dataTable>

<#if parentPojo.shortName!=pojo.shortName>
        <div class="actionButtons">
            <s:button id="${'#'}{${homeName}.instance.${property.name} != null ? 'changeParent' : 'selectParent'}" value="${'#'}{${homeName}.instance.${property.name} != null ? 'Change' : 'Select'} ${property.name}"
                       view="/${parentPageName}List.xhtml">
                <f:param name="from" value="${pageName}Edit"/>
            </s:button>
        </div>

</#if>
    </div>
    </rich:tab>
</#if>
<#if c2h.isOneToManyCollection(property)>

    <rich:tab label="${label(property.name)}">
        <h:form styleClass="association" id="${property.name}Children">

<#assign childPojo = c2j.getPOJOClass(property.value.element.associatedClass)>
<#assign childPageName = childPojo.shortName>
<#assign childEditPageName = childPojo.shortName + "Edit">
<#assign childName = childPojo.shortName?uncap_first>
            <h:outputText value="There are no ${property.name} associated with this ${componentName}."
                       rendered="${'#'}{empty ${homeName}.${property.name}}"/>

            <rich:dataTable value="${'#'}{${homeName}.${property.name}}"
                           var="_${childName}"
                      rendered="${'#'}{not empty ${homeName}.${property.name}}"
                    rowClasses="rvgRowOne,rvgRowTwo"
                            id="${property.name}Table">
<#foreach childProperty in childPojo.allPropertiesIterator>
<#if !c2h.isCollection(childProperty) && !isToOne(childProperty) && childProperty != childPojo.versionProperty!>
<#if childPojo.isComponent(childProperty)>
<#foreach componentProperty in childProperty.value.propertyIterator>
                <rich:column sortBy="${'#'}{_${childName}.${childProperty.name}.${componentProperty.name}}">
                    <f:facet name="header">${label(componentProperty.name)}</f:facet>
                    <@outputValue property=componentProperty expression="${'#'}{_${childName}.${childProperty.name}.${componentProperty.name}}" indent=20/>
                </rich:column>
</#foreach>
<#else>
                <rich:column sortBy="${'#'}{_${childName}.${childProperty.name}}">
                    <f:facet name="header">${label(childProperty.name)}</f:facet>
                    <@outputValue property=childProperty expression="${'#'}{_${childName}.${childProperty.name}}" indent=20/>
                </rich:column>
</#if>
</#if>
</#foreach>
            </rich:dataTable>

        </h:form>

        <f:subview rendered="${'#'}{${homeName}.managed}" id="${property.name}">
        <div class="actionButtons">
            <s:button id="add${childName}"
                   value="Add ${childName}"
                    view="/${childEditPageName}.xhtml"
             propagation="none">
                 <f:param name="${componentName}${pojo.identifierProperty.name?cap_first}"
                         value="${'#'}{${homeName}.instance.${pojo.identifierProperty.name}}"/>
                 <f:param name="${childName}From" value="${entityName}"/>
            </s:button>
        </div>
        </f:subview>
    </rich:tab>
</#if>
</#foreach>
<#if hasAssociations>
</rich:tabPanel>
</#if>
</ui:define>

</ui:composition>
