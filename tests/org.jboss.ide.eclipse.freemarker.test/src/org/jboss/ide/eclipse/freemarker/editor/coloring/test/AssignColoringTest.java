package org.jboss.ide.eclipse.freemarker.editor.coloring.test;

import org.eclipse.swt.custom.StyleRange;
import org.jboss.ide.eclipse.freemarker.model.test.AbstractDirectiveTest;
import org.jboss.ide.eclipse.freemarker.model.test.AssignmentDirectiveTest;

public class AssignColoringTest extends AbstractColoringTest {

	@Override
	protected String getTestDirectoryName() {
		return AbstractDirectiveTest.TEST_DIRECTORY;
	}

	@Override
	protected String getTestTemplateName() {
		return AssignmentDirectiveTest.TEST_FTL_FILE;
	}

	public void testAssignColoring() {
		StyleRange[] expected = new StyleRangeArrayBuilder()
				.directive(8) // <#assign
				.otherExpPart(1) // <whitespace>
				.variable(3) // key
				.otherExpPart(1) // =
				.string(5) // "val"
				.directive(1) // >
				.text(1) // <whitespace>
				.directive(8) // <#assign
				.otherExpPart(1) // <whitespace>
				.variable(7) // seasons
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
				.directive(8) // <#assign
				.otherExpPart(1) // <whitespace>
				.variable(7) // counter
				.otherExpPart(3) // =
				.variable(7) // counter
				.otherExpPart(11) // ?number + 1
				.directive(1) // >
				.text(1) // <whitespace>
				.directive(8) // <#assign
				.otherExpPart(1) // <whitespace>
				.variable(7) // counter
				.otherExpPart(2) // ++
				.directive(1) // >
				.text(1) // <whitespace>
				.directive(8) // <#assign
				.otherExpPart(1) // <whitespace>
				.variable(7) // counter
				.otherExpPart(5) // *= 2
				.directive(1) // >
				.text(1) // <whitespace>
				.directive(8) // <#assign
				.otherExpPart(3) // <whitespace>
				.variable(4) // days
				.otherExpPart(4) // = [
				.string(4) // "Mo"
				.otherExpPart(2) // ,
				.string(4) // "Tu"
				.otherExpPart(2) // ,
				.string(4) // "We"
				.otherExpPart(2) // ,
				.string(4) // "Th"
				.otherExpPart(2) // ,
				.string(4) // "Fr"
				.otherExpPart(2) // ,
				.string(4) // "Sa"
				.otherExpPart(2) // ,
				.string(4) // "Su"
				.otherExpPart(4) // ]
				.variable(7) // counter
				.otherExpPart(3) // =
				.variable(7) // counter
				.otherExpPart(5) // + 1
				.directive(1) // >
				.text(1) // <whitespace>
				.directive(7) // <#macro
				.otherExpPart(1) // <whitespace>
				.variable(7) // myMacro
				.directive(1) // >
				.text(3) // foo
				.directive(9) // </#macro>
				.text(1) // <whitespace>
				.directive(8) // <#assign
				.otherExpPart(1) // <whitespace>
				.variable(16) // formattedSeasons
				.directive(1) // >
				.text(3) // <whitespace>
				.directive(6) // <#list
				.otherExpPart(1) // <whitespace>
				.variable(7) // seasons
				.otherExpPart(1) // <whitespace>
				.keyword(2) // as
				.otherExpPart(1) // <whitespace>
				.variable(1) // s
				.directive(1) // >
				.text(5) // <whitespace>
				.interpolation(2) // ${
				.variable(1) // s
				.interpolation(1) // }
				.text(1) // <whitespace>
				.directive(9) // <@myMacro
				.otherExpPart(1) // <whitespace>
				.directive(2) // />
				.text(3) // <whitespace>
				.directive(8) // </#list>
				.text(1) // <whitespace>
				.directive(10) // </#assign>
				.text(18) // Number of words:
				.interpolation(2) // ${
				.variable(16) // formattedSeasons
				.otherExpPart(15) // ?word_list?size
				.interpolation(1) // }
				.text(1) // <whitespace>
				.interpolation(2) // ${
				.variable(16) // formattedSeasons
				.interpolation(1) // }
				.text(1) // <whitespace>
				.directive(8) // <#assign
				.otherExpPart(1) // <whitespace>
				.variable(5) // hello
				.otherExpPart(1) // =
				.string(16) // "Hello ${user}!"
				.directive(1) // >
				.text(1) // <whitespace>
				.interpolation(2) // ${
				.variable(5) // hello
				.interpolation(1) // }
				.build();
		validateColoring(expected);
	}

}
