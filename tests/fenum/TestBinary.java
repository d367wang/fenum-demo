import org.checkerframework.checker.fenum.qual.Fenum;

@SuppressWarnings("fenum:assignment.type.incompatible")
public class TestBinary {
    public final @Fenum("A") int ACONST1 = 1;
    public final @Fenum("A") int ACONST2 = 2;
    public final @Fenum("A") int ACONST3 = 3;

    public final @Fenum("B") int BCONST1 = 4;
    public final @Fenum("B") int BCONST2 = 5;
    public final @Fenum("B") int BCONST3 = 6;
}

class FenumUserTestBinary {
    // int field1 = TestBinary.ACONST1 + TestBinary.ACONST2;
    int field1 = new TestBinary().ACONST1 + new TestBinary().ACONST1;

    // :: error: (assignment.type.incompatible)
    @Fenum("B") int field2 = new TestBinary().ACONST1 + new TestBinary().BCONST1;

    void foo(TestBinary t) {
        // :: error: (assignment.type.incompatible)
        int state1 = t.ACONST2 + t.ACONST3;

        int state2 = field1 * 2;

        // :: error: (assignment.type.incompatible)
        int state3 = field2 * 2;

        if (t.ACONST1 < t.ACONST2) {
            // ok
        }

        // :: error: (binary.type.incompatible)
        if (t.ACONST1 < t.BCONST2) {}
        // :: error: (binary.type.incompatible)
        if (t.ACONST1 == t.BCONST2) {}

        // :: error: (binary.type.incompatible)
        if (t.ACONST1 < 5) {}
        // :: error: (binary.type.incompatible)
        if (t.ACONST1 == 5) {}
    }
}
