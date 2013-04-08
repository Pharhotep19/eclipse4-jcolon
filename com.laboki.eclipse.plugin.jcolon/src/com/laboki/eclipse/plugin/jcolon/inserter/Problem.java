package com.laboki.eclipse.plugin.jcolon.inserter;

import java.util.Arrays;
import java.util.List;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.compiler.IProblem;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IEditorPart;

import com.laboki.eclipse.plugin.jcolon.Instance;

final class Problem implements Instance {

	private static final String SEMICOLON = ";";
	private static final String PERIOD = ".";
	private static final List<Integer> PROBLEM_IDS = Arrays.asList(IProblem.ParsingErrorInsertToComplete, IProblem.ParsingErrorInsertToCompletePhrase, IProblem.ParsingErrorInsertToCompleteScope, IProblem.ParsingErrorInsertTokenAfter, IProblem.ParsingErrorInsertTokenBefore);
	private IEditorPart editor = EditorContext.getEditor();
	private IDocument document = EditorContext.getDocument(this.editor);
	private final ICompilationUnit compilationUnit = JavaCore.createCompilationUnitFrom(EditorContext.getFile(this.editor));

	public int location() {
		return this.getSemiColonProblem().getSourceEnd() + 1;
	}

	public boolean isMissingSemiColonError() {
		if (this.getSemiColonProblem() == null) return false;
		return true;
	}

	private IProblem getSemiColonProblem() {
		for (final IProblem problem : this.createCompilationUnitNode().getProblems())
			if (this.isValidSemiColonProblem(problem)) return problem;
		return null;
	}

	private CompilationUnit createCompilationUnitNode() {
		final ASTParser parser = ASTParser.newParser(AST.JLS4);
		parser.setSource(this.compilationUnit);
		return (CompilationUnit) parser.createAST(null);
	}

	private boolean isValidSemiColonProblem(final IProblem problem) {
		if (this.lineEndsWithSemiColon(problem) || this.lineEndsWithPeriod(problem)) return false;
		if (Problem.isConstructorDeclaration(problem)) return false;
		if (Problem.isSemiColonProblem(problem)) return true;
		return false;
	}

	private static boolean isConstructorDeclaration(final IProblem problem) {
		for (final String string : problem.getArguments())
			if (string.trim().equals("ConstructorDeclaration")) return true;
		return false;
	}

	private boolean lineEndsWithSemiColon(final IProblem problem) {
		return this.lineEndsWith(problem, Problem.SEMICOLON);
	}

	private boolean lineEndsWithPeriod(final IProblem problem) {
		return this.lineEndsWith(problem, Problem.PERIOD);
	}

	private boolean lineEndsWith(final IProblem problem, final String string) {
		try {
			return this.getLineString(problem).endsWith(string);
		} catch (final BadLocationException e) {
			return false;
		}
	}

	private String getLineString(final IProblem problem) throws BadLocationException {
		final int lineNumber = problem.getSourceLineNumber() - 1;
		return this.document.get(this.document.getLineOffset(lineNumber), this.document.getLineLength(lineNumber)).trim();
	}

	private static boolean isSemiColonProblem(final IProblem problem) {
		return Problem.isInsertionErrorID(problem) && Problem.containsSemiColon(problem);
	}

	private static boolean isInsertionErrorID(final IProblem problem) {
		return Problem.PROBLEM_IDS.contains(problem.getID());
	}

	private static boolean containsSemiColon(final IProblem problem) {
		for (final String string : problem.getArguments())
			if (string.trim().equals(Problem.SEMICOLON)) return true;
		return false;
	}

	@Override
	public Instance begin() {
		return this;
	}

	@Override
	public Instance end() {
		this.editor = null;
		this.document = null;
		return this;
	}
}
