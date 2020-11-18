package fenum;

import com.sun.source.tree.AnnotationTree;
import com.sun.source.tree.AssignmentTree;
import com.sun.source.tree.BinaryTree;
import com.sun.source.tree.CaseTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.LiteralTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.ModifiersTree;
import com.sun.source.tree.NewArrayTree;
import com.sun.source.tree.NewClassTree;
import com.sun.source.tree.SwitchTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.Tree.Kind;
import com.sun.source.tree.VariableTree;

import checkers.inference.InferenceChecker;
import checkers.inference.InferenceVisitor;

import javax.lang.model.element.*;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.*;

import fenum.qual.IntDef;
import fenum.qual.Fenum;
import fenum.qual.FenumPattern;

import org.checkerframework.common.basetype.BaseAnnotatedTypeFactory;
import org.checkerframework.common.basetype.BaseTypeChecker;
import org.checkerframework.common.basetype.BaseTypeVisitor;
import org.checkerframework.framework.type.AnnotatedTypeMirror;
import org.checkerframework.framework.type.AnnotatedTypeMirror.AnnotatedDeclaredType;
import org.checkerframework.framework.type.AnnotatedTypeMirror.AnnotatedExecutableType;
import org.checkerframework.framework.type.QualifierHierarchy;

import org.checkerframework.framework.type.ElementAnnotationApplier;
import org.checkerframework.javacutil.ElementUtils;
import org.checkerframework.framework.type.AnnotatedTypeMirror.AnnotatedExecutableType;
import org.checkerframework.framework.type.AnnotatedTypeMirror.AnnotatedTypeVariable;


import org.checkerframework.javacutil.AnnotationBuilder;
import org.checkerframework.javacutil.AnnotationUtils;
import org.checkerframework.javacutil.TreeUtils;
import org.checkerframework.javacutil.TypesUtils;
import org.checkerframework.javacutil.TypeAnnotationUtils;

public class FenumVisitor extends InferenceVisitor<FenumChecker, BaseAnnotatedTypeFactory> {

    /**
     * Map from the @Fenum name to the corresponding {@link FenumSet}.
     */
    private Map<String, FenumSet> fakeEnumsMap;

    private FenumHelper helper;
  
    public FenumVisitor(
            FenumChecker checker,
            InferenceChecker ichecker,
            BaseAnnotatedTypeFactory factory,
            boolean infer) {
        super(checker, ichecker, factory, infer);
        fakeEnumsMap = new HashMap<>();
        helper = new FenumHelper(atypeFactory);
        
    }

    @Override
    public void processClassTree(ClassTree classTree) {
      /*
        TypeElement elt = TreeUtils.elementFromDeclaration(classTree);
        IntDef anno = elt.getAnnotation(IntDef.class);
        if (anno != null) {
            ModifiersTree modifiersTree = classTree.getModifiers();
            List<? extends AnnotationTree> annotationTrees = modifiersTree.getAnnotations();
            for (AnnotationTree annotationTree : annotationTrees) {

            }

            super.processClassTree(classTree);
            return;
        }

        List<VariableTree> allFields = TreeUtils.getAllFields(classTree);
        for (VariableTree field : allFields) {
            if (helper.isFenumDeclaration(field)) {
                // If the field is a fake enum constant, validate the constants within the same fake
                // enum type. If the validation succeeds, add the value of it to the fake enum set.
                // Otherwise an InvalidFenumConstantException will be raised
                try {
                    validateAndAddFenumValue(field);

                } catch (InvalidFenumConstantException e) {

                }
            }
        }

        // After all valid fake enum constants are grouped and loaded, check if the constant values
        // in each fake enum set are consecutive
        for (Map.Entry<String, FenumSet> entry : fakeEnumsMap.entrySet()) {
            FenumSet fenumSet = entry.getValue();

            if (!fenumSet.isValidated() && fenumSet.getPattern() == FenumPattern.CONSECUTIVE) {
                try {
                    validateConsecutivePattern(fenumSet);

                } catch (InvalidFenumConstantException e) {

                }
            }
            fenumSet.setValidated(true);
        }
      */ 
        super.processClassTree(classTree);
    }


    @Override
    public Void visitVariable(VariableTree node, Void p) {
        AnnotatedTypeMirror type = atypeFactory.getAnnotatedType(node);
        mainIsNot(type, realChecker.FENUM_BOTTOM, "bottom.not.allowed", node);

        return super.visitVariable(node, p);

    }


  /*
  @Override
  public Void visitMethod(MethodTree node, Void p) {
    final ExecutableElement methodElem = TreeUtils.elementFromDeclaration(node);
    TypeMirror tm = methodElem.asType();
    TypeMirror unannotated = TypeAnnotationUtils.unannotatedType(tm);
    System.out.println("\n\nannotated type mirror: " + tm.toString());
    System.out.println("unannotated type mirror: " + unannotated.toString());

    AnnotatedTypeMirror type = AnnotatedTypeMirror.createType(unannotated, atypeFactory, ElementUtils.isTypeDeclaration(methodElem));
    List<AnnotatedTypeVariable> typeVar = ((AnnotatedExecutableType)type).getTypeVariables();
    
    System.out.println("get type variable:");
    if (typeVar.size() != 0) {
      for(AnnotatedTypeVariable tv : typeVar) {
        System.out.println(tv.toString());
      }
    } else {
      System.out.println("get no variable");
    }

    //ElementAnnotationApplier.apply(type, methodElem, atypeFactory);
    return super.visitMethod(node, p);
        
  }
  */

  
    @Override
    public Void visitMethod(MethodTree node, Void p) {
        final ExecutableElement methodElem = TreeUtils.elementFromDeclaration(node);
        AnnotatedExecutableType methodType = (AnnotatedExecutableType) atypeFactory.getAnnotatedType(node);

        if (infer) {
            mainIsNot(methodType.getReturnType(), realChecker.FENUM_BOTTOM, "bottom.not.allowed", node);
            for (AnnotatedTypeMirror t : methodType.getParameterTypes()) {
                mainIsNot(t, realChecker.FENUM_BOTTOM, "bottom.not.allowed", node);

            }
        }
        return super.visitMethod(node, p);
    }
  

    @Override
    public Void visitLiteral(LiteralTree node, Void p) {
        super.visitLiteral(node, p);

        System.out.println("\n=============visiting literal" + node.getValue() + " =====================");
        AnnotatedTypeMirror atm = atypeFactory.getAnnotatedType(node);
        System.out.println("atm: " + atm.toString());
        System.out.println("=========================================================================\n");

        return null;

    }

  
  
    @Override
    public Void visitSwitch(SwitchTree node, Void p) {
        ExpressionTree expr = node.getExpression();
        AnnotatedTypeMirror exprType = atypeFactory.getAnnotatedType(expr);

        /*
        System.out.println("\n\nswitch expr " + expr.toString() + ": ");
        System.out.println("atm: " + exprType.toString());
        System.out.println("underlying type: " + exprType.getUnderlyingType().toString() + ", " + exprType.getUnderlyingType().getClass().getSimpleName());
        System.out.println("\n");
        */
        
        for (CaseTree caseExpr : node.getCases()) {
            ExpressionTree realCaseExpr = caseExpr.getExpression();
            if (realCaseExpr != null) {
                AnnotatedTypeMirror caseType = atypeFactory.getAnnotatedType(realCaseExpr);

                /*
                System.out.println("case expr " + realCaseExpr.toString() + ": ");
                System.out.println("atm: " + caseType.toString());
                System.out.println("underlying type: " + caseType.getUnderlyingType().toString() + ", " + caseType.getUnderlyingType().getClass().getSimpleName());

                boolean sameUnderlyingType = exprType.getUnderlyingType().equals(caseType.getUnderlyingType());
                if (sameUnderlyingType) {
                  System.out.println("same underlying type");
                                  
                } else {
                  System.out.println("underlying type not same");
                }

                boolean arePrimeAnnosEqual = AnnotationUtils.areSame(exprType.getAnnotations(), caseType.getAnnotations());
                if (arePrimeAnnosEqual) {
                  System.out.println("Prime Annos Equal");
                                  
                } else {
                  System.out.println("Prime Annos not Equal");
                                  
                }
                System.out.println("");
                 */
                
                areEqual(exprType, caseType, "equality.constraint.unsatisfiable", caseExpr);
                
              /*
              this.commonAssignmentCheck(
                  exprType, caseType, caseExpr, "switch.type.incompatible"
                                         );               */
            }
        }
        return super.visitSwitch(node, p);
    }

    /*
    @Override
    public Void visitSwitch(SwitchTree node, Void p) {
        ExpressionTree expr = node.getExpression();
        AnnotatedTypeMirror exprType = atypeFactory.getAnnotatedType(expr);

        for (CaseTree caseExpr : node.getCases()) {
            ExpressionTree realCaseExpr = caseExpr.getExpression();
            if (realCaseExpr != null) {
                AnnotatedTypeMirror caseType = atypeFactory.getAnnotatedType(realCaseExpr);

                this.commonAssignmentCheck(
                        exprType, caseType, caseExpr, "switch.type.incompatible");
            }
        }
        return super.visitSwitch(node, p);
    }

     */


    @Override
    public Void visitBinary(BinaryTree node, Void p) {
        AnnotatedTypeMirror lhsAtm = atypeFactory.getAnnotatedType(node.getLeftOperand());
        AnnotatedTypeMirror rhsAtm = atypeFactory.getAnnotatedType(node.getRightOperand());

        Kind opKind = node.getKind();
        switch (opKind) {
            case EQUAL_TO:
            case NOT_EQUAL_TO:
                // The Fenum Checker is only concerned with primitive types, so just check that
                // the primary annotations are equivalent.
                
                Set<AnnotationMirror> lhs = lhsAtm.getEffectiveAnnotations();
                Set<AnnotationMirror> rhs = rhsAtm.getEffectiveAnnotations();
                QualifierHierarchy qualHierarchy = atypeFactory.getQualifierHierarchy();

                areComparable(lhsAtm, rhsAtm, "comparable.constraint.unsatisfiable", node);

                // TODO: flow-sensitive refinement
                break;

            default:
                checker.reportError(node, "binary.operation.unsupported", lhsAtm, rhsAtm);

        }
        return super.visitBinary(node, p);
    }

    /*
    @Override
    public Void visitBinary(BinaryTree node, Void p) {

        // The Fenum Checker is only concerned with primitive types, so just check that
        // the primary annotations are equivalent.
        AnnotatedTypeMirror lhsAtm = atypeFactory.getAnnotatedType(node.getLeftOperand());
        AnnotatedTypeMirror rhsAtm = atypeFactory.getAnnotatedType(node.getRightOperand());

        Set<AnnotationMirror> lhs = lhsAtm.getEffectiveAnnotations();
        Set<AnnotationMirror> rhs = rhsAtm.getEffectiveAnnotations();
        QualifierHierarchy qualHierarchy = atypeFactory.getQualifierHierarchy();
        if (!(qualHierarchy.isSubtype(lhs, rhs) || qualHierarchy.isSubtype(rhs, lhs))) {
            checker.reportError(node, "binary.type.incompatible", lhsAtm, rhsAtm);
        }
        return super.visitBinary(node, p);
    }
    */

    @Override
    public Void visitAnnotation(AnnotationTree node, Void p) {
        if (AnnotationUtils.areSameByName(TreeUtils.annotationFromAnnotationTree(node), IntDef.class.getCanonicalName())) {
            List<? extends ExpressionTree> args = node.getArguments();
            for (ExpressionTree exprTree : args) {
                assert exprTree instanceof AssignmentTree;
                AssignmentTree tree = (AssignmentTree) exprTree;

                if (tree.getVariable().toString().equals("value")) {
                    // Converts the referenced constants in the typedef annotation to the corresponding Fenum type
                    assert tree.getExpression() instanceof NewArrayTree;
                    List<? extends ExpressionTree> initializers = ((NewArrayTree) tree.getExpression()).getInitializers();
                    for (ExpressionTree initializer : initializers) {
                        VariableElement varElem = (VariableElement) TreeUtils.elementFromUse(initializer);

                        System.out.println(varElem.toString() + "\n");

                        // TODO: use unchecked pattern for now, should check flag and set pattern
                        AnnotationMirror anno = helper.genereateFenumAnnotation(generateFenumTypeString());

                        // Replaces the annotation of referenced constant initializer with @Fenum annotation
                        AnnotatedTypeMirror atm = atypeFactory.getAnnotatedType(varElem);
                        atm.replaceAnnotation(anno);
                    }
                }
            }
        }
        return super.visitAnnotation(node, p);
    }


    @Override
    protected boolean skipReceiverSubtypeCheck(MethodInvocationTree node, AnnotatedTypeMirror methodDefinitionReceiver, AnnotatedTypeMirror methodCallReceiver) {
      return true;
          
    }


    /**
     * Generates the string that uniquely identifies a fake enum type by combing the enclosing class name.
     *
     * @return string that uniquely identifies a fake enum type
     */
    private String generateFenumTypeString() {
        // Use the class name as the fenum type string
        ClassTree enclosingClass = TreeUtils.enclosingClass(getCurrentPath());
        System.out.println("enclosing class: " + enclosingClass.getSimpleName());
        return enclosingClass.getSimpleName().toString();
    }


    @Override
    protected void checkConstructorInvocation(
            AnnotatedDeclaredType dt, AnnotatedExecutableType constructor, NewClassTree src) {
        // Ignore the default annotation on the constructor
    }

    @Override
    protected void checkConstructorResult(
            AnnotatedExecutableType constructorType, ExecutableElement constructorElement) {
        // Skip this check
    }


    @Override
    protected Set<? extends AnnotationMirror> getExceptionParameterLowerBoundAnnotations() {
//        return Collections.singleton(atypeFactory.FENUM_UNQUALIFIED);
        return Collections.singleton(realChecker.FENUM_UNQUALIFIED);
    }

    // TODO: should we require a match between switch expression and cases?

    @Override
    public boolean isValidUse(
            AnnotatedDeclaredType declarationType, AnnotatedDeclaredType useType, Tree tree) {
        // The checker calls this method to compare the annotation used in a
        // type to the modifier it adds to the class declaration. As our default
        // modifier is FenumBottom, this results in an error when a non-subtype
        // is used. Can we use FenumTop as default instead?
        return true;
    }

    /**
     * Adds a new integer constant into the corresponding fake enum set. If the fake enum set
     * already the constant value, issues an error and aborts.
     *
     * @param node the variable tree that identifies the new constant to be added
     */
    private void validateAndAddFenumValue(VariableTree node) {
        // Get items of the variable that need to be validated, including the underlying type,
        // elements of the annotation, etc.
        AnnotatedTypeMirror atm = atypeFactory.getAnnotatedType(node);
        TypeMirror underlyingType = atm.getUnderlyingType();
        AnnotationMirror anno = atm.getAnnotation(Fenum.class);
        String fenumName = FenumHelper.getFenumName(anno);
        FenumPattern fenumPattern = FenumHelper.getFenumPattern(anno);
        Object constValue = FenumHelper.getConstantValue(node, underlyingType);

        FenumSet fenumSet;
        if (fakeEnumsMap.containsKey(fenumName)) {
            // If the fake enum to add the constant already exists, validate the underlying type,
            // fenum pattern and constant value
            fenumSet = fakeEnumsMap.get(fenumName);
            validateUnderlyingType(node, fenumSet, underlyingType);
            validateFenumPattern(node, fenumSet, fenumPattern);
            validateValue(node, fenumSet, constValue);

        } else {
            // If the fake enum to add the constant does not exist, validate only the underlying
            // type. If succeeds, create the new corresponding fake enum set
            validateUnderlyingType(node, null, underlyingType);
            // underlying type is either int or String
            if (underlyingType.getKind() == TypeKind.INT) {
                fenumSet = new FenumSet(fenumName, FenumSet.TYPE_INTEGER, fenumPattern, node);

            } else {
                validateStringFenumPattern(node, fenumPattern);
                fenumSet = new FenumSet(fenumName, FenumSet.TYPE_STRING, fenumPattern, node);
            }

            // Validation passes (otherwise an InvalidFenumConstantException will be raised
            // halfway). Add the new constant value
            fakeEnumsMap.put(fenumName, fenumSet);
        }
        fenumSet.addValue(constValue);
    }

    /**
     * Validates the underlying type of a fake enum constant before it is added to the fake enum
     * set, and issue an error if the pattern is invalid.
     *
     * @param node     the underlying fake enum constant of which the pattern is validated. This is used
     *                 only for the source position in the error message
     * @param fenumSet the fake enum set to which the fake enum constant is added
     * @param tm       the underlying type to be validated
     */
    private void validateUnderlyingType(VariableTree node, FenumSet fenumSet, TypeMirror tm) {
        if (!(tm.getKind() == TypeKind.INT || TypesUtils.isString(tm))) {
            reportErrorAndThrowException(node, "unsupported.constant.type", tm);
        }

        if (fenumSet != null) {
            if ((fenumSet.isInteger() && !(tm.getKind() == TypeKind.INT))
                    || (fenumSet.isString() && !(TypesUtils.isString(tm)))) {
                reportErrorAndThrowException(node, "constant.type.conflict", tm);
            }
        }
    }

    /**
     * Validates the pattern of a String-typed fake enum constant. The pattern is only allowed to be
     * {@link FenumPattern.UNCHECKED}
     *
     * @param node         the underlying fake enum constant of which the pattern is validated. This is used
     *                     only for the source position in the error message
     * @param fenumPattern the pattern to be validated
     */
    private void validateStringFenumPattern(VariableTree node, FenumPattern fenumPattern) {
        if (fenumPattern != FenumPattern.UNCHECKED) {
            reportErrorAndThrowException(node, "fenum.pattern.not.applicable", fenumPattern);
        }
    }

    /**
     * Validates the pattern of a fake enum constant before it is added to the fake enum set, and
     * issue an error if the pattern is invalid.
     *
     * @param node     the underlying fake enum constant of which the pattern is validated. This is used
     *                 only for the source position in the error message
     * @param fenumSet the fake enum set to which the fake enum constant is added
     * @param pattern  the pattern to be validated
     */
    private void validateFenumPattern(VariableTree node, FenumSet fenumSet, FenumPattern pattern) {
        if (pattern != fenumSet.getPattern()) {
            reportErrorAndThrowException(
                    node, "fenum.pattern.conflict", fenumSet.getPattern(), pattern);
        }

        if (fenumSet.isString()) {
            validateStringFenumPattern(node, fenumSet.getPattern());
        }
    }

    /**
     * Validates the value of a fake enum constant before it is added to the fake enum set, and
     * issue an error if the value is invalid.
     *
     * @param node     the underlying fake enum constant of which the value is validated. This is used
     *                 only for the source position in the error message
     * @param fenumSet the fake enum set to which the fake enum constant is added
     * @param value    the value to be validated
     */
    private void validateValue(VariableTree node, FenumSet fenumSet, Object value) {
        if (fenumSet.containsValue(value)) {
            reportErrorAndThrowException(
                    node, "duplicated.constant.value", value, fenumSet.getName());
        }

        if (fenumSet.getPattern() == FenumPattern.FLAG) {
            if (!FenumHelper.isPowerOfTwo((int) value)) {
                reportErrorAndThrowException(node, "not.powerof.two", value);
            }
        }
    }

    /**
     * Validates a consecutive-pattern fake enum set after all the constants are added, and issue an
     * error if the items in the fenum set are not consecutive.
     *
     * @param fenumSet the fake enum set to be validated
     */
    private void validateConsecutivePattern(FenumSet fenumSet) {
        if (!fenumSet.isConsecutive()) {
            reportErrorAndThrowException(
                    fenumSet.getStart(), "nonconsecutive.constant.values", fenumSet.getName());
        }
    }

    /**
     * Reports an error. By default, prints it to the screen via the compiler's internal messager.
     *
     * @param node   the source position of the reported error
     * @param errMsg the message key
     * @param args   arguments for interpolation in the string corresponding to the given message key
     */
    private void reportErrorAndThrowException(Tree node, String errMsg, Object... args) {
        checker.reportError(node, errMsg, args);
        throw new InvalidFenumConstantException();
    }
}
