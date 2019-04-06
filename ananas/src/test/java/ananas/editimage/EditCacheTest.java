package ananas.editimage;

import android.graphics.Bitmap;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import iamutkarshtiwari.github.io.ananas.editimage.widget.EditCache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(manifest= Config.NONE)
public class EditCacheTest {
    private EditCache editCache;

    private int flag1 = 100;
    private int flag2 = 100;

    @Before
    public void prepare() {
        editCache = new EditCache();
    }

    @Test
    public void testEditCacheSizeDefault() {
        assertEquals(EditCache.EDIT_CACHE_SIZE, editCache.getEditCacheSize());
    }

    @Test
    public void testEditCacheSize() {
        int size = 100;
        EditCache cache = new EditCache(size);
        assertEquals(size, cache.getEditCacheSize());
    }

    @Test
    public void test_EditCache_PushSame() {
        Bitmap bit1 = createTestBitmap();
        Bitmap bit2 = createTestBitmap();
        Bitmap bit3 = createTestBitmap();
        Bitmap bit4 = createTestBitmap();
        Bitmap bit5 = createTestBitmap();
        editCache.push(bit1);
        editCache.push(bit2);
        editCache.push(bit3);
        editCache.push(bit4);
        editCache.push(bit5);
        editCache.push(bit1);
        editCache.push(bit2);
        editCache.push(bit3);
        editCache.push(bit4);
        editCache.push(bit5);

        assertEquals(5, editCache.getSize());
    }

    public void test_EditCache_Pop() {
        prepareCache();
        assertEquals(editCache.getEditCacheSize(), editCache.getSize());
    }

    public void test_EditCache_Pop2() {
        prepareCache();
        Bitmap insertBit = Bitmap.createBitmap(1024, 500, Bitmap.Config.ARGB_8888);
        editCache.push(insertBit);
    }

    @Test
    public void test_EditCache_Pop3() {
        prepareCache();
        Bitmap insertBit1 = Bitmap.createBitmap(1024, 500, Bitmap.Config.ARGB_8888);
        editCache.push(insertBit1);

        Bitmap insertBit2 = Bitmap.createBitmap(1024, 500, Bitmap.Config.ARGB_8888);
        editCache.push(insertBit2);
        assertEquals(editCache.getEditCacheSize(), editCache.getSize());
    }


    @Test
    public void test_EditCache_PushSame2() {
        Bitmap bit1 = createTestBitmap();
        Bitmap bit2 = createTestBitmap();
        editCache.push(bit2);
        editCache.push(bit1);
        editCache.push(bit2);
        assertEquals(2, editCache.getSize());
    }

    @Test
    public void test_EditCache_removeAll() {
        prepare();
        Bitmap bit1 = createTestBitmap();
        Bitmap bit2 = createTestBitmap();
        Bitmap bit3 = createTestBitmap();
        Bitmap bit4 = createTestBitmap();
        Bitmap bit5 = createTestBitmap();

        editCache.push(bit1);
        editCache.push(bit2);
        editCache.push(bit3);
        editCache.push(bit4);
        editCache.push(bit5);

        editCache.removeAll();

        assertEquals(0, editCache.getSize());
        assertTrue(bit1.isRecycled());
        assertTrue(bit2.isRecycled());
        assertTrue(bit3.isRecycled());
        assertTrue(bit4.isRecycled());
        assertTrue(bit5.isRecycled());
    }


    @Test
    public void test_EditCache_get_next_and_pre_Bitmap() {
        Bitmap bit1 = createTestBitmap();
        Bitmap bit2 = createTestBitmap();
        Bitmap bit3 = createTestBitmap();
        Bitmap bit4 = createTestBitmap();
        Bitmap bit5 = createTestBitmap();
        Bitmap bit6 = createTestBitmap();
        Bitmap bit7 = createTestBitmap();
        Bitmap bit8 = createTestBitmap();
        Bitmap bit9 = createTestBitmap();
        Bitmap bit10 = createTestBitmap();
        Bitmap bit11 = createTestBitmap();
        Bitmap bit12 = createTestBitmap();
        Bitmap bit13 = createTestBitmap();
        Bitmap bit14 = createTestBitmap();
        Bitmap bit15 = createTestBitmap();

        editCache.push(bit1);
        editCache.push(bit2);
        editCache.push(bit3);
        editCache.push(bit4);
        editCache.push(bit5);
        editCache.push(bit6);
        editCache.push(bit7);
        editCache.push(bit8);
        editCache.push(bit9);
        editCache.push(bit10);
        editCache.push(bit11);
        editCache.push(bit12);
        editCache.push(bit13);
        editCache.push(bit14);
        editCache.push(bit15);
    }

    @Test
    public void test_EditCache_isPointTo() {
        assertTrue(editCache.isPointToLastElem());
        Bitmap bit1 = createTestBitmap();
        editCache.push(bit1);
        assertTrue(editCache.isPointToLastElem());

        editCache.push(createTestBitmap());
        assertTrue(editCache.isPointToLastElem());

        Bitmap bit2 = createTestBitmap();
        editCache.push(bit2);
        Bitmap bit3 = createTestBitmap();
        editCache.push(bit3);
        assertTrue(editCache.isPointToLastElem());
    }

    @Test
    public void test_EditCache_isPointLast_First() {
        Bitmap bit1 = createTestBitmap();
        editCache.push(bit1);
        Bitmap bit2 = createTestBitmap();
        editCache.push(bit2);
        Bitmap bit3 = createTestBitmap();
        editCache.push(bit3);
        Bitmap bit4 = createTestBitmap();
        editCache.push(bit4);

        assertTrue(editCache.isPointToLastElem());
    }

    @Test
    public void test_EditCache_Current_Point1() {
        prepareCache();
        Bitmap bit1 = createTestBitmap();
        editCache.push(bit1);
        Bitmap bit2 = createTestBitmap();
        editCache.push(bit2);
        Bitmap bit3 = createTestBitmap();
        editCache.push(bit3);
        Bitmap bit4 = createTestBitmap();
        editCache.push(bit4);

        assertEquals(bit3, editCache.getNextCurrentBit());
        assertEquals(bit2, editCache.getNextCurrentBit());
        assertEquals(bit1, editCache.getNextCurrentBit());
        assertEquals(bit2, editCache.getPreCurrentBit());
        assertEquals(bit3, editCache.getPreCurrentBit());
        assertEquals(bit4, editCache.getPreCurrentBit());
    }

    @Test
    public void test_EditCache_Current_Point2() {
        Bitmap bit1 = createTestBitmap();
        editCache.push(bit1);
        Bitmap bit2 = createTestBitmap();
        editCache.push(bit2);
        Bitmap bit3 = createTestBitmap();
        editCache.push(bit3);
        Bitmap bit4 = createTestBitmap();
        editCache.push(bit4);

        assertEquals(bit3, editCache.getNextCurrentBit());
        assertEquals(bit2, editCache.getNextCurrentBit());
        assertEquals(bit1, editCache.getNextCurrentBit());
    }

    @Test
    public void test_EditCache_Current_Point3() {
        int size = 5;
        prepareCache(size);
        Bitmap bit1 = createTestBitmap();
        editCache.push(bit1);
        Bitmap bit2 = createTestBitmap();
        editCache.push(bit2);
        Bitmap bit3 = createTestBitmap();
        editCache.push(bit3);
        Bitmap bit4 = createTestBitmap();
        editCache.push(bit4);

        assertEquals(bit4, editCache.getCurBit());
        assertEquals(bit3, editCache.getNextCurrentBit());
        assertEquals(bit2, editCache.getNextCurrentBit());
        assertEquals(bit1, editCache.getNextCurrentBit());
        editCache.getNextCurrentBit();

        Bitmap bit = createTestBitmap();
        editCache.push(bit);
        assertEquals(size, editCache.getCur());
        assertEquals(bit , editCache.getCurBit());
    }

    @Test
    public void test_EditCache_Current_Point4() {
        int size = 5;
        prepareCache(size);
        Bitmap bit1 = createTestBitmap();
        editCache.push(bit1);
        Bitmap bit2 = createTestBitmap();
        editCache.push(bit2);
        Bitmap bit3 = createTestBitmap();
        editCache.push(bit3);
        Bitmap bit4 = createTestBitmap();
        editCache.push(bit4);

        assertEquals(bit4, editCache.getCurBit());
        assertEquals(bit3, editCache.getNextCurrentBit());
        assertEquals(bit2, editCache.getNextCurrentBit());
        assertEquals(bit1, editCache.getNextCurrentBit());
        assertEquals(size, editCache.getCur());
    }

    @Test
    public void test_EditCache_Current_Point5() {
        int size = 10;
        prepareCache(size);
        Bitmap bit1 = createTestBitmap();
        editCache.push(bit1);
        Bitmap bit2 = createTestBitmap();
        editCache.push(bit2);
        Bitmap bit3 = createTestBitmap();
        editCache.push(bit3);
        Bitmap bit4 = createTestBitmap();
        editCache.push(bit4);

        Bitmap bit5 = createTestBitmap();
        editCache.push(bit5);

        assertEquals(bit4, editCache.getNextCurrentBit());
        assertEquals(bit3, editCache.getNextCurrentBit());
        assertEquals(bit2, editCache.getNextCurrentBit());
        assertEquals(bit1, editCache.getNextCurrentBit());
        editCache.getNextCurrentBit();

        Bitmap bit6 = createTestBitmap();
        editCache.push(bit6);
        assertEquals(bit6, editCache.getCurBit());
        assertTrue(bit1.isRecycled());
        assertTrue(bit2.isRecycled());
        assertTrue(bit3.isRecycled());
        assertTrue(bit4.isRecycled());

        assertEquals(6, editCache.getSize());
    }

    @Test
    public void test_EditCache_observer1(){
        EditCache.ListModify modify1 = cache -> flag1 = 200;

        EditCache.ListModify modify2 = cache -> flag2 = 200;

        editCache.addObserver(modify1);
        editCache.addObserver(modify2);
        editCache.addObserver(modify1);


        Bitmap bit1 = createTestBitmap();
        editCache.push(bit1);
        assertEquals(200 , flag1);
        assertEquals(200 , flag2);

        flag1 = 100;
        flag2 = 100;
        Bitmap bitmap = editCache.getNextCurrentBit();

        assertEquals(200 , flag1);
        assertEquals(200 , flag2);

        flag1 = 300;
        flag2 = 300;
        editCache.getPreCurrentBit();
        assertEquals(200 , flag1);
        assertEquals(200 , flag2);

        flag1 = 400;
        flag2 = 400;
        editCache.removeObserver(modify1);
        Bitmap testBitmap = createTestBitmap();
        editCache.push(bit1);
        assertEquals(400 , flag1);
        assertEquals(200 , flag2);
    }

    @Test
    public void test_EditCache_observer2(){
        flag1 = 100;
        flag2 = 100;
        EditCache.ListModify modify1 = cache -> flag1 = 200;

        EditCache.ListModify modify2 = cache -> flag2 = 200;

        editCache.addObserver(modify1);
        editCache.addObserver(modify2);

        flag1 = 100;
        flag2 = 100;

        prepareCache(10);
        assertEquals(200 , flag1);
        assertEquals(200 , flag2);

        editCache.removeObserver(modify1);
        editCache.removeObserver(modify2);
        flag1 = 2;
        flag2 = 2;
        editCache.getNextCurrentBit();
        editCache.getPreCurrentBit();
        assertEquals(2, flag1);
        assertEquals(2, flag2);
    }

    @Test
    public void test_check(){
        assertFalse(editCache.checkNextBitExist());
        assertFalse(editCache.checkPreBitExist());
        prepareCache(3);
        assertTrue(editCache.checkNextBitExist());
        assertFalse(editCache.checkPreBitExist());

        assertTrue(editCache.checkNextBitExist());
        editCache.getNextCurrentBit();
        assertTrue(editCache.checkPreBitExist());
        assertTrue(editCache.checkNextBitExist());
        editCache.getNextCurrentBit();
        assertFalse(editCache.checkNextBitExist());
        editCache.getNextCurrentBit();

        prepareCache(1);
        assertFalse(editCache.checkPreBitExist());
        assertFalse(editCache.checkNextBitExist());
    }

    private void prepareCache(int size) {
        for (int i = 0; i < size; i++) {
            Bitmap bit1 = createTestBitmap();
            editCache.push(bit1);
        }
    }

    private void prepareCache() {
        Bitmap bit1 = createTestBitmap();
        Bitmap bit2 = createTestBitmap();
        Bitmap bit3 = createTestBitmap();
        Bitmap bit4 = createTestBitmap();
        Bitmap bit5 = createTestBitmap();
        Bitmap bit6 = createTestBitmap();
        Bitmap bit7 = createTestBitmap();
        Bitmap bit8 = createTestBitmap();
        Bitmap bit9 = createTestBitmap();
        Bitmap bit10 = createTestBitmap();
        Bitmap bit11 = createTestBitmap();
        Bitmap bit12 = createTestBitmap();
        Bitmap bit13 = createTestBitmap();
        Bitmap bit14 = createTestBitmap();
        Bitmap bit15 = createTestBitmap();

        editCache.push(bit1);
        editCache.push(bit2);
        editCache.push(bit3);
        editCache.push(bit4);
        editCache.push(bit5);
        editCache.push(bit6);
        editCache.push(bit7);
        editCache.push(bit8);
        editCache.push(bit9);
        editCache.push(bit10);
        editCache.push(bit11);
        editCache.push(bit12);
        editCache.push(bit13);
        editCache.push(bit14);
        editCache.push(bit15);
    }

    private Bitmap createTestBitmap() {
        return Bitmap.createBitmap(500, 500, Bitmap.Config.ARGB_8888);
    }
}
