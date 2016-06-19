/******************************************************************************* 
 * Copyright (c) 2015 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.ide.eclipse.freemarker.model.interpolation;

public enum BuiltInInfo {

    /** {@link freemarker.core.BuiltInsForNumbers$absBI */
    ABS("abs", FTLType.NUMBER, FTLType.NUMBER, false), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForNodes$ancestorsBI */
    ANCESTORS("ancestors", FTLType.NODE, FTLType.SEQUENCE, false), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForMultipleTypes$apiBI */
    API("api", FTLType.ANY, FTLType.HASH, false), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForStringsMisc$booleanBI */
    BOOLEAN("boolean", FTLType.STRING, FTLType.BOOLEAN, false), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForNumbers$byteBI */
    BYTE("byte", FTLType.NUMBER, FTLType.NUMBER, false), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForMultipleTypes$cBI */
    C("c", new FTLType[] { FTLType.STRING, FTLType.NUMBER }, FTLType.STRING, false), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForStringsBasic$cap_firstBI */
    CAP_FIRST("capFirst", FTLType.STRING, FTLType.STRING, false), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForStringsBasic$capitalizeBI */
    CAPITALIZE("capitalize", FTLType.STRING, FTLType.STRING, false), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForNumbers$ceilingBI */
    CEILING("ceiling", FTLType.NUMBER, FTLType.NUMBER, false), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForNodes$childrenBI */
    CHILDREN("children", FTLType.NODE, FTLType.SEQUENCE, false), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForStringsBasic$chop_linebreakBI */
    CHOP_LINEBREAK("chopLinebreak", FTLType.STRING, FTLType.STRING, false), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForSequences$chunkBI */
    CHUNK("chunk", FTLType.SEQUENCE, FTLType.SEQUENCE, true), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForStringsBasic$containsBI */
    CONTAINS("contains", FTLType.STRING, FTLType.BOOLEAN, true), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForLoopVariables$counterBI */
    COUNTER("counter", FTLType.LOOP_VAR, FTLType.NUMBER, false), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForMultipleTypes$dateBI */
    DATE("date", new FTLType[] { FTLType.STRING, FTLType.DATE_LIKE }, FTLType.DATE_LIKE, false), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForDates$dateType_if_unknownBI */
    DATE_IF_UNKNOWN("dateIfUnknown", FTLType.DATE_LIKE, FTLType.DATE_LIKE, false), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForMultipleTypes$dateBI */
    DATETIME("datetime", new FTLType[] { FTLType.STRING, FTLType.DATE_LIKE }, FTLType.DATE_LIKE, false), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForDates$dateType_if_unknownBI */
    DATETIME_IF_UNKNOWN("datetimeIfUnknown", new FTLType[] { FTLType.STRING, FTLType.DATE_LIKE }, FTLType.DATE_LIKE, false), //$NON-NLS-1$
    /** {@link freemarker.core.ExistenceBuiltins$defaultBI */
    DEFAULT("default", new FTLType[] { FTLType.ANY }, FTLType.ANY, true, true), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForNumbers$doubleBI */
    DOUBLE("double", FTLType.NUMBER, FTLType.NUMBER, false), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForStringsBasic$ends_withBI */
    ENDS_WITH("endsWith", FTLType.STRING, FTLType.BOOLEAN, true), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForStringsBasic$ensure_ends_withBI */
    ENSURE_ENDS_WITH("ensureEndsWith", FTLType.STRING, FTLType.STRING, true), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForStringsBasic$ensure_starts_withBI */
    ENSURE_STARTS_WITH("ensureStartsWith", FTLType.STRING, FTLType.STRING, true), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForOutputFormatRelated$escBI */
    ESC("esc", new FTLType[] { FTLType.STRING, FTLType.MARKUP_OUTPUT }, FTLType.MARKUP_OUTPUT, false), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForStringsMisc$evalBI */
    EVAL("eval", FTLType.STRING, FTLType.ANY, false), //$NON-NLS-1$
    /** {@link freemarker.core.ExistenceBuiltins$existsBI */
    EXISTS("exists", new FTLType[] { FTLType.ANY }, FTLType.BOOLEAN, false, true), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForSequences$firstBI */
    FIRST("first", FTLType.SEQUENCE, FTLType.ANY, false), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForNumbers$floatBI */
    FLOAT("float", FTLType.NUMBER, FTLType.NUMBER, false), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForNumbers$floorBI */
    FLOOR("floor", FTLType.NUMBER, FTLType.NUMBER, false), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForStringsRegexp$groupsBI */
    GROUPS("groups", FTLType.REG_EXP_MATCH, FTLType.SEQUENCE, false), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForMultipleTypes$has_apiBI */
    HAS_API("hasApi", FTLType.ANY, FTLType.BOOLEAN, false), //$NON-NLS-1$
    /** {@link freemarker.core.ExistenceBuiltins$has_contentBI */
    HAS_CONTENT("hasContent", FTLType.ANY, FTLType.BOOLEAN, false), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForLoopVariables$has_nextBI */
    HAS_NEXT("hasNext", FTLType.LOOP_VAR, FTLType.BOOLEAN, false), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForStringsEncoding$htmlBI */
    HTML("webSafe", new FTLType[] { FTLType.STRING }, FTLType.STRING, false, true), //$NON-NLS-1$
    /** {@link freemarker.core.ExistenceBuiltins$if_existsBI */
    IF_EXISTS("ifExists", new FTLType[] { FTLType.ANY }, FTLType.ANY, false, true), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForLoopVariables$indexBI */
    INDEX("index", FTLType.LOOP_VAR, FTLType.NUMBER, false), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForStringsBasic$index_ofBI */
    INDEX_OF("indexOf", FTLType.STRING, FTLType.BOOLEAN, false), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForNumbers$intBI */
    INT("int", FTLType.NUMBER, FTLType.NUMBER, false), //$NON-NLS-1$
    /** {@link freemarker.core.Interpret */
    INTERPRET("interpret", FTLType.STRING, FTLType.DIRECTIVE, false), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForMultipleTypes$is_booleanBI */
    IS_BOOLEAN("isBoolean", FTLType.ANY, FTLType.BOOLEAN, false), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForMultipleTypes$is_collectionBI */
    IS_COLLECTION("isCollection", FTLType.ANY, FTLType.BOOLEAN, false), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForMultipleTypes$is_collection_exBI */
    IS_COLLECTION_EX("isCollectionEx", FTLType.ANY, FTLType.BOOLEAN, false), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForMultipleTypes$is_dateLikeBI */
    IS_DATE("isDate", new FTLType[] { FTLType.ANY }, FTLType.BOOLEAN, false, true), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForMultipleTypes$is_dateLikeBI */
    IS_DATE_LIKE("isDateLike", FTLType.ANY, FTLType.BOOLEAN, false), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForMultipleTypes$is_dateOfTypeBI */
    IS_DATE_ONLY("isDateOnly", FTLType.ANY, FTLType.BOOLEAN, false), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForMultipleTypes$is_dateOfTypeBI */
    IS_DATETIME("isDatetime", FTLType.ANY, FTLType.BOOLEAN, false), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForMultipleTypes$is_directiveBI */
    IS_DIRECTIVE("isDirective", FTLType.ANY, FTLType.BOOLEAN, false), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForMultipleTypes$is_enumerableBI */
    IS_ENUMERABLE("isEnumerable", FTLType.ANY, FTLType.BOOLEAN, false), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForLoopVariables$is_even_itemBI */
    IS_EVEN_ITEM("isEvenItem", FTLType.LOOP_VAR, FTLType.BOOLEAN, false), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForLoopVariables$is_firstBI */
    IS_FIRST("isFirst", FTLType.LOOP_VAR, FTLType.BOOLEAN, false), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForMultipleTypes$is_hashBI */
    IS_HASH("isHash", FTLType.ANY, FTLType.BOOLEAN, false), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForMultipleTypes$is_hash_exBI */
    IS_HASH_EX("isHashEx", FTLType.ANY, FTLType.BOOLEAN, false), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForMultipleTypes$is_indexableBI */
    IS_INDEXABLE("isIndexable", FTLType.ANY, FTLType.BOOLEAN, false), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForNumbers$is_infiniteBI */
    IS_INFINITE("isInfinite", FTLType.NUMBER, FTLType.BOOLEAN, true|false), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForLoopVariables$is_lastBI */
    IS_LAST("isLast", FTLType.LOOP_VAR, FTLType.BOOLEAN, false), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForMultipleTypes$is_macroBI */
    IS_MACRO("isMacro", FTLType.ANY, FTLType.BOOLEAN, false), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForMultipleTypes$is_markup_outputBI */
    IS_MARKUP_OUTPUT("isMarkupOutput", FTLType.ANY, FTLType.BOOLEAN, false), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForMultipleTypes$is_methodBI */
    IS_METHOD("isMethod", FTLType.ANY, FTLType.BOOLEAN, false), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForNumbers$is_nanBI */
    IS_NAN("isNan", FTLType.NUMBER, FTLType.BOOLEAN, false), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForMultipleTypes$is_nodeBI */
    IS_NODE("isNode", FTLType.ANY, FTLType.BOOLEAN, false), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForMultipleTypes$is_numberBI */
    IS_NUMBER("isNumber", FTLType.ANY, FTLType.BOOLEAN, false), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForLoopVariables$is_odd_itemBI */
    IS_ODD_ITEM("isOddItem", FTLType.LOOP_VAR, FTLType.BOOLEAN, false), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForMultipleTypes$is_sequenceBI */
    IS_SEQUENCE("isSequence", FTLType.ANY, FTLType.BOOLEAN, false), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForMultipleTypes$is_stringBI */
    IS_STRING("isString", FTLType.ANY, FTLType.BOOLEAN, false), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForMultipleTypes$is_dateOfTypeBI */
    IS_TIME("isTime", FTLType.ANY, FTLType.BOOLEAN, false), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForMultipleTypes$is_transformBI */
    IS_TRANSFORM("isTransform", FTLType.ANY, FTLType.BOOLEAN, false), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForMultipleTypes$is_dateOfTypeBI */
    IS_UNKNOWN_DATE_LIKE("isUnknownDateLike", FTLType.ANY, FTLType.BOOLEAN, false), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForDates$iso_BI */
    ISO("iso", new FTLType[] { FTLType.DATE_LIKE }, FTLType.STRING, true, true), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForDates$iso_BI */
    ISO_H("isoH", new FTLType[] { FTLType.DATE_LIKE }, FTLType.STRING, true, true), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForDates$iso_BI */
    ISO_H_NZ("isoHNZ", new FTLType[] { FTLType.DATE_LIKE }, FTLType.STRING, true, true), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForDates$iso_utc_or_local_BI */
    ISO_LOCAL("isoLocal", new FTLType[] { FTLType.DATE_LIKE }, FTLType.STRING, false, true), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForDates$iso_utc_or_local_BI */
    ISO_LOCAL_H("isoLocalH", new FTLType[] { FTLType.DATE_LIKE }, FTLType.STRING, false, true), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForDates$iso_utc_or_local_BI */
    ISO_LOCAL_H_NZ("isoLocalHNZ", new FTLType[] { FTLType.DATE_LIKE }, FTLType.STRING, false, true), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForDates$iso_utc_or_local_BI */
    ISO_LOCAL_M("isoLocalM", new FTLType[] { FTLType.DATE_LIKE }, FTLType.STRING, false, true), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForDates$iso_utc_or_local_BI */
    ISO_LOCAL_M_NZ("isoLocalMNZ", new FTLType[] { FTLType.DATE_LIKE }, FTLType.STRING, false, true), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForDates$iso_utc_or_local_BI */
    ISO_LOCAL_MS("isoLocalMs", new FTLType[] { FTLType.DATE_LIKE }, FTLType.STRING, false, true), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForDates$iso_utc_or_local_BI */
    ISO_LOCAL_MS_NZ("isoLocalMsNZ", new FTLType[] { FTLType.DATE_LIKE }, FTLType.STRING, false, true), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForDates$iso_utc_or_local_BI */
    ISO_LOCAL_NZ("isoLocalNZ", new FTLType[] { FTLType.DATE_LIKE }, FTLType.STRING, false, true), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForDates$iso_BI */
    ISO_M("isoM", new FTLType[] { FTLType.DATE_LIKE }, FTLType.STRING, true, true), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForDates$iso_BI */
    ISO_M_NZ("isoMNZ", new FTLType[] { FTLType.DATE_LIKE }, FTLType.STRING, true, true), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForDates$iso_BI */
    ISO_MS("isoMs", new FTLType[] { FTLType.DATE_LIKE }, FTLType.STRING, true, true), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForDates$iso_BI */
    ISO_MS_NZ("isoMsNZ", new FTLType[] { FTLType.DATE_LIKE }, FTLType.STRING, true, true), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForDates$iso_BI */
    ISO_NZ("isoNZ", new FTLType[] { FTLType.DATE_LIKE }, FTLType.STRING, true, true), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForDates$iso_utc_or_local_BI */
    ISO_UTC("isoUtc", new FTLType[] { FTLType.DATE_LIKE }, FTLType.STRING, false, true), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForDates$iso_utc_or_local_BI */
    ISO_UTC_FZ("isoUtcFZ", new FTLType[] { FTLType.DATE_LIKE }, FTLType.STRING, false, true), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForDates$iso_utc_or_local_BI */
    ISO_UTC_H("isoUtcH", new FTLType[] { FTLType.DATE_LIKE }, FTLType.STRING, false, true), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForDates$iso_utc_or_local_BI */
    ISO_UTC_H_NZ("isoUtcHNZ", new FTLType[] { FTLType.DATE_LIKE }, FTLType.STRING, false, true), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForDates$iso_utc_or_local_BI */
    ISO_UTC_M("isoUtcM", new FTLType[] { FTLType.DATE_LIKE }, FTLType.STRING, false, true), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForDates$iso_utc_or_local_BI */
    ISO_UTC_M_NZ("isoUtcMNZ", new FTLType[] { FTLType.DATE_LIKE }, FTLType.STRING, false, true), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForDates$iso_utc_or_local_BI */
    ISO_UTC_MS("isoUtcMs", new FTLType[] { FTLType.DATE_LIKE }, FTLType.STRING, false, true), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForDates$iso_utc_or_local_BI */
    ISO_UTC_MS_NZ("isoUtcMsNZ", new FTLType[] { FTLType.DATE_LIKE }, FTLType.STRING, false, true), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForDates$iso_utc_or_local_BI */
    ISO_UTC_NZ("isoUtcNZ", new FTLType[] { FTLType.DATE_LIKE }, FTLType.STRING, false, true), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForLoopVariables$item_cycleBI */
    ITEM_CYCLE("itemCycle", FTLType.LOOP_VAR, FTLType.ANY, true), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForLoopVariables$item_parityBI */
    ITEM_PARITY("itemParity", FTLType.LOOP_VAR, FTLType.STRING, false), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForLoopVariables$item_parity_capBI */
    ITEM_PARITY_CAP("itemParityCap", FTLType.LOOP_VAR, FTLType.STRING, false), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForStringsEncoding$j_stringBI */
    J_STRING("jString", FTLType.STRING, FTLType.STRING, false), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForSequences$joinBI */
    JOIN("join", FTLType.SEQUENCE, FTLType.STRING, true), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForStringsEncoding$js_stringBI */
    JS_STRING("jsString", FTLType.STRING, FTLType.STRING, false), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForStringsEncoding$json_stringBI */
    JSON_STRING("jsonString", FTLType.STRING, FTLType.STRING, false), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForStringsBasic$keep_afterBI */
    KEEP_AFTER("keepAfter", FTLType.STRING, FTLType.STRING, true), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForStringsBasic$keep_after_lastBI */
    KEEP_AFTER_LAST("keepAfterLast", FTLType.STRING, FTLType.STRING, true), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForStringsBasic$keep_beforeBI */
    KEEP_BEFORE("keepBefore", FTLType.STRING, FTLType.STRING, true), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForStringsBasic$keep_before_lastBI */
    KEEP_BEFORE_LAST("keepBeforeLast", FTLType.STRING, FTLType.STRING, true), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForHashes$keysBI */
    KEYS("keys", FTLType.HASH, FTLType.SEQUENCE, false), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForSequences$lastBI */
    LAST("last", FTLType.SEQUENCE, FTLType.ANY, false), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForStringsBasic$index_ofBI */
    LAST_INDEX_OF("lastIndexOf", FTLType.STRING, FTLType.NUMBER, true), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForStringsBasic$padBI */
    LEFT_PAD("leftPad", FTLType.STRING, FTLType.STRING, true), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForStringsBasic$lengthBI */
    LENGTH("length", FTLType.STRING, FTLType.NUMBER, false), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForNumbers$longBI */
    LONG("long", FTLType.NUMBER, FTLType.NUMBER, false), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForNumbers$lower_abcBI */
    LOWER_ABC("lowerAbc", FTLType.NUMBER, FTLType.STRING, false), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForStringsBasic$lower_caseBI */
    LOWER_CASE("lowerCase", FTLType.STRING, FTLType.STRING, false), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForMarkupOutputs$markup_stringBI */
    MARKUP_STRING("markupString", FTLType.MARKUP_OUTPUT, FTLType.STRING, false), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForStringsRegexp$matchesBI */
    MATCHES("matches", FTLType.STRING, FTLType.BOOLEAN, true), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForMultipleTypes$namespaceBI */
    NAMESPACE("namespace", FTLType.MACRO_OR_FUNCTION, FTLType.HASH, false), //$NON-NLS-1$
    /** {@link freemarker.core.NewBI */
    NEW("new", FTLType.STRING, FTLType.ANY, true), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForOutputFormatRelated$no_escBI */
    NO_ESC("noEsc", new FTLType[] { FTLType.STRING, FTLType.MARKUP_OUTPUT }, FTLType.MARKUP_OUTPUT, false), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForNodes$node_nameBI */
    NODE_NAME("nodeName", FTLType.NODE, FTLType.STRING, false), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForNodes$node_namespaceBI */
    NODE_NAMESPACE("nodeNamespace", FTLType.NODE, FTLType.STRING, false), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForNodes$node_typeBI */
    NODE_TYPE("nodeType", FTLType.NODE, FTLType.STRING, false), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForStringsMisc$numberBI */
    NUMBER("number", FTLType.STRING, FTLType.NUMBER, false), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForNumbers$number_to_dateBI */
    NUMBER_TO_DATE("numberToDate", FTLType.NUMBER, FTLType.DATE_LIKE, false), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForNumbers$number_to_dateBI */
    NUMBER_TO_DATETIME("numberToDatetime", FTLType.NUMBER, FTLType.DATE_LIKE, false), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForNumbers$number_to_dateBI */
    NUMBER_TO_TIME("numberToTime", FTLType.NUMBER, FTLType.DATE_LIKE, false), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForNodes$parentBI */
    PARENT("parent", FTLType.NODE, FTLType.NODE, false), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForStringsBasic$remove_beginningBI */
    REMOVE_BEGINNING("removeBeginning", FTLType.STRING, FTLType.STRING, true), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForStringsBasic$remove_endingBI */
    REMOVE_ENDING("removeEnding", FTLType.STRING, FTLType.STRING, true), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForStringsRegexp$replace_reBI */
    REPLACE("replace", FTLType.STRING, FTLType.STRING, true), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForSequences$reverseBI */
    REVERSE("reverse", FTLType.SEQUENCE, FTLType.SEQUENCE, true), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForStringsBasic$padBI */
    RIGHT_PAD("rightPad", FTLType.STRING, FTLType.STRING, true), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForNodes$rootBI */
    ROOT("root", FTLType.NODE, FTLType.NODE, false), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForNumbers$roundBI */
    ROUND("round", FTLType.NUMBER, FTLType.NUMBER, false), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForStringsEncoding$rtfBI */
    RTF("rtf", FTLType.STRING, FTLType.STRING, false), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForSequences$seq_containsBI */
    SEQ_CONTAINS("seqContains", FTLType.SEQUENCE, FTLType.BOOLEAN, true), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForSequences$seq_index_ofBI */
    SEQ_INDEX_OF("seqIndexOf", FTLType.SEQUENCE, FTLType.NUMBER, true), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForSequences$seq_index_ofBI */
    SEQ_LAST_INDEX_OF("seqLastIndexOf", FTLType.SEQUENCE, FTLType.NUMBER, true), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForNumbers$shortBI */
    SHORT("short", FTLType.NUMBER, FTLType.NUMBER, false), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForMultipleTypes$sizeBI */
    SIZE("size", new FTLType[] { FTLType.SEQUENCE, FTLType.HASH }, FTLType.NUMBER, false), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForSequences$sortBI */
    SORT("sort", FTLType.SEQUENCE, FTLType.SEQUENCE, true), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForSequences$sort_byBI */
    SORT_BY("sortBy", FTLType.SEQUENCE, FTLType.SEQUENCE, true), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForStringsBasic$split_BI */
    SPLIT("split", FTLType.STRING, FTLType.SEQUENCE, true), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForStringsBasic$starts_withBI */
    STARTS_WITH("startsWith", FTLType.STRING, FTLType.BOOLEAN, true), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForMultipleTypes$stringBI */
    STRING("string", new FTLType[] { FTLType.NUMBER, FTLType.BOOLEAN, FTLType.DATE_LIKE }, FTLType.STRING, false), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForStringsBasic$substringBI */
    SUBSTRING("substring", new FTLType[] { FTLType.STRING }, FTLType.STRING, true, true), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsWithParseTimeParameters$switch_BI */
    SWITCH("switch", FTLType.ANY, FTLType.ANY, true), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsWithParseTimeParameters$then_BI */
    THEN("then", FTLType.BOOLEAN, FTLType.ANY, true), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForMultipleTypes$dateBI */
    TIME("time", new FTLType[] { FTLType.DATE_LIKE, FTLType.STRING }, FTLType.DATE_LIKE, false), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForDates$dateType_if_unknownBI */
    TIME_IF_UNKNOWN("timeIfUnknown", FTLType.DATE_LIKE, FTLType.DATE_LIKE, false), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForStringsBasic$trimBI */
    TRIM("trim", FTLType.STRING, FTLType.STRING, false), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForStringsBasic$uncap_firstBI */
    UNCAP_FIRST("uncapFirst", FTLType.STRING, FTLType.STRING, false), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForNumbers$upper_abcBI */
    UPPER_ABC("upperAbc", FTLType.NUMBER, FTLType.STRING, false), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForStringsBasic$upper_caseBI */
    UPPER_CASE("upperCase", FTLType.STRING, FTLType.STRING, false), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForStringsEncoding$urlBI */
    URL("url", FTLType.STRING, FTLType.STRING, false), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForStringsEncoding$urlPathBI */
    URL_PATH("urlPath", FTLType.STRING, FTLType.STRING, false), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForHashes$valuesBI */
    VALUES("values", FTLType.HASH, FTLType.SEQUENCE, false), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForStringsEncoding$htmlBI */
    WEB_SAFE("webSafe", new FTLType[] { FTLType.STRING }, FTLType.STRING, false, true), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForStringsBasic$word_listBI */
    WORD_LIST("wordList", FTLType.STRING, FTLType.SEQUENCE, false), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForStringsEncoding$xhtmlBI */
    XHTML("xhtml", FTLType.STRING, FTLType.STRING, false), //$NON-NLS-1$
    /** {@link freemarker.core.BuiltInsForStringsEncoding$xmlBI */
    XML("xml", FTLType.STRING, FTLType.STRING, false), //$NON-NLS-1$
    ;
    
    private final String snakeCaseName;
    private final String camelCaseName;
    private final FTLType[] leftHandTypes;
    private final FTLType returnType;
    private final boolean parameterListRequired;
    private final boolean deprecated;
    
    private BuiltInInfo(String camleCaseName, FTLType leftHandTypes, FTLType returnType, boolean parameterListRequired) {
        this(camleCaseName, new FTLType[] { leftHandTypes }, returnType, parameterListRequired);
    }

    private BuiltInInfo(String camleCaseName, FTLType[] leftHandTypes, FTLType returnType, boolean parameterListRequired,
            boolean deprecated) {
        snakeCaseName = name().toLowerCase();
        this.camelCaseName = camleCaseName;
        this.leftHandTypes = leftHandTypes;
        this.returnType = returnType;
        this.parameterListRequired = parameterListRequired;
        this.deprecated = deprecated;
    }
    
    private BuiltInInfo(String camleCaseName, FTLType[] leftHandTypes, FTLType returnType, boolean parameterListRequired) {
        this(camleCaseName, leftHandTypes, returnType, parameterListRequired, false);
    }

    public String getSnakeCaseName() {
        return snakeCaseName;
    }

    public String getCamelCaseName() {
        return camelCaseName;
    }

    public FTLType[] getLeftHandTypes() {
        return leftHandTypes;
    }

    public FTLType getReturnType() {
        return returnType;
    }

    public boolean isParameterListRequired() {
        return parameterListRequired;
    }

    public boolean isDeprecated() {
        return deprecated;
    }
    
}
