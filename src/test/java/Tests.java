import org.example.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Tests {

    int[] set;

    @BeforeEach
    void setUp() {
        set = new int[]{-1, -1, 2, 3, 3, -1, 4, -1, 2, -1};
    }

    @AfterEach
    void tearDown() {
        int[] set = null;
    }

    @Nested
    class ImagerViewer {

        @Test
        void find() {
            assertEquals(3, Set.find(set, 6));
        }

        @Test
        void union() {
            Set.union(set, 6, 8);
            assertEquals(3, Set.find(set, 6));
            assertEquals(3, Set.find(set, 8));
        }
    }
}