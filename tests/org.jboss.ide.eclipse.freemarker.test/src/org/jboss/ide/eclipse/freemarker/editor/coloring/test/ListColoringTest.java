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

	public void testListColoring() {
		StyleRange[] expected = new StyleRangeArrayBuilder()
				.directive(8) // <#assign
				.otherExpPart(1) // <whitespace>
				.variable(3) // seq
				.otherExpPart(4) // = [
				.string(8) // "winter"
				.otherExpPart(2) // ,
				.string(8) // "spring"
				.otherExpPart(2) // ,
				.string(8) // "summer"
				.otherExpPart(2) // ,
				.string(8) // "autumn"
				.otherExpPart(1) // ]
				.directive(1) // >
				.text(1) // <whitespace>
				.directive(6) // <#list
				.otherExpPart(1) // <whitespace>
				.variable(3) // seq
				.otherExpPart(1) // <whitespace>
				.keyword(2) // as
				.otherExpPart(1) // <whitespace>
				.variable(1) // x
				.directive(1) // >
				.text(3) // <whitespace>
				.interpolation(2) // ${
				.variable(7) // x_index
				.otherExpPart(4) // + 1
				.interpolation(1) // }
				.text(2) // .
				.interpolation(2) // ${
				.variable(1) // x
				.interpolation(1) // }
				.directive(4) // <#if
				.otherExpPart(1) // <whitespace>
				.variable(10) // x_has_next
				.directive(1) // >
				.text(1) // ,
				.directive(6) // </#if>
				.text(1) // <whitespace>
				.directive(8) // </#list>
				.text(3) // <whitespace>
				.directive(6) // <#list
				.otherExpPart(6) // 1..3
				.keyword(2) // as
				.otherExpPart(1) // <whitespace>
				.variable(1) // n
				.directive(1) // >
				.text(3) // <whitespace>
				.directive(6) // <#list
				.otherExpPart(6) // 1..3
				.keyword(2) // as
				.otherExpPart(1) // <whitespace>
				.variable(1) // m
				.directive(1) // >
				.text(16) // list item #
				.interpolation(2) // ${
				.variable(1) // n
				.interpolation(1) // }
				.text(1) // x
				.interpolation(2) // ${
				.variable(1) // m
				.interpolation(1) // }
				.text(3) // <whitespace>
				.directive(8) // </#list>
				.text(1) // <whitespace>
				.directive(8) // </#list>
				.text(2) // <whitespace>
				.directive(6) // <#list
				.otherExpPart(1) // <whitespace>
				.variable(3) // seq
				.otherExpPart(1) // <whitespace>
				.keyword(2) // as
				.otherExpPart(1) // <whitespace>
				.variable(1) // x
				.directive(1) // >
				.text(3) // <whitespace>
				.interpolation(2) // ${
				.variable(1) // x
				.interpolation(1) // }
				.text(3) // <whitespace>
				.directive(4) // <#if
				.otherExpPart(1) // <whitespace>
				.variable(1) // x
				.otherExpPart(3) // =
				.string(8) // "spring"
				.directive(1) // >
				.directive(8) // <#break>
				.directive(6) // </#if>
				.text(1) // <whitespace>
				.directive(8) // </#list>
				.text(2) // <whitespace>
				.directive(8) // <#assign
				.otherExpPart(1) // <whitespace>
				.variable(1) // x
				.directive(1) // >
				.text(3) // <whitespace>
				.directive(6) // <#list
				.otherExpPart(6) // 1..3
				.keyword(2) // as
				.otherExpPart(1) // <whitespace>
				.variable(1) // n
				.directive(1) // >
				.text(16) // list item #
				.interpolation(2) // ${
				.variable(1) // n
				.interpolation(1) // }
				.text(3) // <whitespace>
				.directive(8) // </#list>
				.text(1) // <whitespace>
				.directive(10) // </#assign>
				.text(18) // Number of words:
				.interpolation(2) // ${
				.variable(1) // x
				.otherExpPart(15) // ?word_list?size
				.interpolation(1) // }
				.text(1) // <whitespace>
				.interpolation(2) // ${
				.variable(1) // x
				.interpolation(1) // }
				.text(29) // 2.3.23 listing directives:
				.directive(6) // <#list
				.otherExpPart(5) // 1..3
				.directive(1) // >
				.text(12) // Items:
				.directive(7) // <#items
				.otherExpPart(1) // <whitespace>
				.keyword(2) // as
				.otherExpPart(1) // <whitespace>
				.variable(1) // n
				.directive(1) // >
				.text(4) // <whitespace>
				.interpolation(2) // ${
				.variable(1) // n
				.interpolation(1) // }
				.directive(6) // <#sep>
				.text(4) // ,
				.directive(9) // </#items>
				.text(1) // <whitespace>
				.directive(7) // <#else>
				.text(13) // No items
				.directive(8) // </#list>
				.text(2) // <whitespace>
				.directive(6) // <#list
				.otherExpPart(6) // 1..3
				.keyword(2) // as
				.otherExpPart(1) // <whitespace>
				.variable(1) // x
				.directive(1) // >
				.interpolation(2) // ${
				.variable(1) // x
				.interpolation(1) // }
				.directive(6) // <#sep>
				.text(2) // ,
				.directive(8) // </#list>
				.text(2) // <whitespace>
				.directive(6) // <#list
				.otherExpPart(6) // 1..3
				.keyword(2) // as
				.otherExpPart(1) // <whitespace>
				.variable(1) // x
				.directive(1) // >
				.text(8) // <div>
				.interpolation(2) // ${
				.variable(1) // x
				.interpolation(1) // }
				.directive(6) // <#sep>
				.text(1) // ,
				.directive(7) // </#sep>
				.text(7) // </div>
				.directive(8) // </#list>
				.build();
		validateColoring(expected);
	}
}
