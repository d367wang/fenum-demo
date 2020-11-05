import org.checkerframework.checker.fenum.qual.Fenum;

public class TestBinary {

    @SuppressWarnings("fenum:assignment.type.incompatible")
    public static final @Fenum("A") int ACONST1 = 1;

    @SuppressWarnings("fenum:assignment.type.incompatible")
    public static final @Fenum("A") int ACONST2 = 2;

    @SuppressWarnings("fenum:assignment.type.incompatible")
    public static final @Fenum("A") int ACONST3 = 3;


    public final @Fenum("B") int BCONST1 = 4;
    public final @Fenum("B") int BCONST2 = 5;
    public final @Fenum("B") int BCONST3 = 6;
}

class FenumUserTestBinary {

    int field1 = TestBinary.ACONST1 + new TestBinary().ACONST1;

    void foo(TestBinary t) {
        // :: error: (binary.operation.unsupported)
        int state1 = t.ACONST2 + t.ACONST3;

        // :: error: (binary.operation.unsupported)
        int state2 = field1 * 2;

        // :: error: (binary.operation.unsupported)
        if (t.ACONST1 < t.ACONST2) {
        }

        // :: error: (binary.type.incompatible)
        if (t.ACONST1 == t.BCONST2) {}

        // :: error: (binary.type.incompatible)
        if (t.ACONST1 == 5) {}
    }
}
