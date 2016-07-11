package org.jboss.ide.eclipse.freemarker.editor.coloring.test;

import org.eclipse.swt.custom.StyleRange;
import org.jboss.ide.eclipse.freemarker.model.test.AbstractDirectiveTest;
import org.jboss.ide.eclipse.freemarker.model.test.ListDirectiveTest;

public class ListColoringTest extends AbstractColoringTest {

	@Override
	protected String getTestDirectoryName() {
		return AbstractDirectiveTest.TEST_DIRECTORY;
	}

	@Override
	protected String getTestTemplateName() {
		return ListDirectiveTest.TEST_FTL_FILE;
	}

	@Override
	protected String getTestProjectName() {
		return AbstractDirectiveTest.TEST_PROJECT;
	}

	public void testListColoring() {
		StyleRange[] expected = new StyleRangeArrayBuilder()
				.directive(16) // <#assign seq = [
				.string(8) // "winter"
				.directive(2) // ,
				.string(8) // "spring"
				.directive(2) // ,
				.string(8) // "summer"
				.directive(2) // ,
				.string(8) // "autumn"
				.directive(2) // ]>
				.text(1) // <whitespace>
				.directive(16) // <#list seq as x>
				.text(3) // <whitespace>
				.interpolation(14) // ${x_index + 1}
				.text(2) // .
				.interpolation(4) // ${x}
				.directive(16) // <#if x_has_next>
				.text(1) // ,
				.directive(6) // </#if>
				.text(1) // <whitespace>
				.directive(8) // </#list>
				.text(3) // <whitespace>
				.directive(17) // <#list 1..3 as n>
				.text(3) // <whitespace>
				.directive(17) // <#list 1..3 as m>
				.text(16) // list item #
				.interpolation(4) // ${n}
				.text(1) // x
				.interpolation(4) // ${m}
				.text(3) // <whitespace>
				.directive(8) // </#list>
				.text(1) // <whitespace>
				.directive(8) // </#list>
				.text(2) // <whitespace>
				.directive(16) // <#list seq as x>
				.text(3) // <whitespace>
				.interpolation(4) // ${x}
				.text(3) // <whitespace>
				.directive(9) // <#if x =
				.string(8) // "spring"
				.directive(1) // >
				.directive(8) // <#break>
				.directive(6) // </#if>
				.text(1) // <whitespace>
				.directive(8) // </#list>
				.text(2) // <whitespace>
				.directive(11) // <#assign x>
				.text(3) // <whitespace>
				.directive(17) // <#list 1..3 as n>
				.text(16) // list item #
				.interpolation(4) // ${n}
				.text(3) // <whitespace>
				.directive(8) // </#list>
				.text(1) // <whitespace>
				.directive(10) // </#assign>
				.text(18) // Number of words:
				.interpolation(19) // ${x?word_list?size}
				.text(1) // <whitespace>
				.interpolation(4) // ${x}
				.text(29) // 2.3.23 listing directives:
				.directive(12) // <#list 1..3>
				.text(12) // Items:
				.directive(13) // <#items as n>
				.text(4) // <whitespace>
				.interpolation(4) // ${n}
				.directive(6) // <#sep>
				.text(4) // ,
				.directive(9) // </#items>
				.text(1) // <whitespace>
				.directive(7) // <#else>
				.text(13) // No items
				.directive(8) // </#list>
				.text(2) // <whitespace>
				.directive(17) // <#list 1..3 as x>
				.interpolation(4) // ${x}
				.directive(6) // <#sep>
				.text(2) // ,
				.directive(8) // </#list>
				.text(2) // <whitespace>
				.directive(17) // <#list 1..3 as x>
				.text(8) // <div>
				.interpolation(4) // ${x}
				.directive(6) // <#sep>
				.text(1) // ,
				.directive(7) // </#sep>
				.text(7) // </div>
				.directive(8) // </#list>
				.build();
		validateColoring(expected);
	}
}
