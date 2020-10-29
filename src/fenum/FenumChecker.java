package fenum;

import fenum.qual.FenumBottom;
import fenum.qual.FenumUnqualified;

import checkers.inference.BaseInferrableChecker;
import checkers.inference.InferenceChecker;
import checkers.inference.InferrableChecker;
import checkers.inference.SlotManager;
import checkers.inference.dataflow.InferenceAnalysis;
import checkers.inference.dataflow.InferenceTransfer;
import checkers.inference.model.ConstraintManager;
import org.checkerframework.common.basetype.BaseAnnotatedTypeFactory;
import org.checkerframework.framework.flow.CFTransfer;

import org.checkerframework.javacutil.AnnotationBuilder;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.util.Elements;


public class FenumChecker extends BaseInferrableChecker {
  
  protected AnnotationMirror FENUM_UNQUALIFIED, FENUM_BOTTOM;

    @Override
    public void initChecker() {
        super.initChecker();

        final Elements elements = processingEnv.getElementUtils();
        FENUM_UNQUALIFIED = AnnotationBuilder.fromClass(elements, FenumUnqualified.class);
        FENUM_BOTTOM = AnnotationBuilder.fromClass(elements, FenumBottom.class);
    }

    @Override
    public FenumVisitor createVisitor(
            InferenceChecker ichecker, BaseAnnotatedTypeFactory factory, boolean infer) {
        return new FenumVisitor(this, ichecker, factory, infer);
    }

    @Override
    public FenumAnnotatedTypeFactory createRealTypeFactory() {
        return new FenumAnnotatedTypeFactory(this);
    }

    @Override
    public CFTransfer createInferenceTransferFunction(InferenceAnalysis analysis) {
        return new InferenceTransfer(analysis);
    }

    @Override
    public FenumInferenceAnnotatedTypeFactory createInferenceATF(
            InferenceChecker inferenceChecker,
            InferrableChecker realChecker,
            BaseAnnotatedTypeFactory realTypeFactory,
            SlotManager slotManager,
            ConstraintManager constraintManager) {
        return new FenumInferenceAnnotatedTypeFactory(
                inferenceChecker,
                realChecker.withCombineConstraints(),
                realTypeFactory,
                realChecker,
                slotManager,
                constraintManager);
    }

    @Override
    public boolean isInsertMainModOfLocalVar() {
        return false;
    }

    @Override
    public boolean withCombineConstraints() {
        return false;
    }
}
