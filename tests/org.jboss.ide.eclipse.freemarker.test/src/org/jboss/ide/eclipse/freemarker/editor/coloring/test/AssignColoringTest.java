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

	@Override
	protected String getTestProjectName() {
		return AbstractDirectiveTest.TEST_PROJECT;
	}

	public void testAssignColoring() {
		StyleRange[] expected = new StyleRangeArrayBuilder().directive(13) // <#assign
																			// key=
				.string(5) // "val"
				.directive(1) // >
				.text(1) // <whitespace>
				.directive(20) // <#assign seasons = [
				.string(8) // "winter"
				.directive(2) // ,
				.string(8) // "spring"
				.directive(2) // ,
				.string(8) // "summer"
				.directive(2) // ,
				.string(8) // "autumn"
				.directive(2) // ]>
				.text(1) // <whitespace>
				.directive(31) // <#assign counter = counter + 1>
				.text(1) // <whitespace>
				.directive(19) // <#assign days = [
				.string(4) // "Mo"
				.directive(2) // ,
				.string(4) // "Tu"
				.directive(2) // ,
				.string(4) // "We"
				.directive(2) // ,
				.string(4) // "Th"
				.directive(2) // ,
				.string(4) // "Fr"
				.directive(2) // ,
				.string(4) // "Sa"
				.directive(2) // ,
				.string(4) // "Su"
				.directive(27) // ] counter = counter + 1 >
				.text(1) // <whitespace>
				.directive(16) // <#macro myMacro>
				.text(3) // foo
				.directive(9) // </#macro>
				.text(1) // <whitespace>
				.directive(26) // <#assign formattedSeasons>
				.text(3) // <whitespace>
				.directive(20) // <#list seasons as s>
				.text(5) // <whitespace>
				.interpolation(4) // ${s}
				.text(1) // <whitespace>
				.directive(12) // <@myMacro />
				.text(3) // <whitespace>
				.directive(8) // </#list>
				.text(1) // <whitespace>
				.directive(10) // </#assign>
				.text(18) // Number of words:
				.interpolation(34) // ${formattedSeasons?word_list?size}
				.text(1) // <whitespace>
				.interpolation(19) // ${formattedSeasons}
				.text(1) // <whitespace>
				.directive(15) // <#assign hello=
				.string(7) // "Hello
				.interpolation(7) // ${user}
				.string(2) // !"
				.directive(1) // >
				.text(1) // <whitespace>
				.interpolation(8) // ${hello}
				.build();
		validateColoring(expected);
	}

}
