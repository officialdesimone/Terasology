/*
 * Copyright 2012 Benjamin Glatzel <benjamin.glatzel@me.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.terasology.math;

import com.google.common.collect.Sets;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Immortius
 */
public class Region3iTest {

    @Test
    public void emptyConstructor()
    {
        Region3i region = new Region3i();
        assertEquals(new Vector3i(), region.size());
        assertTrue(region.isEmpty());
    }

    @Test
    public void testCreateRegionWithMinAndSize()
    {
        List<Vector3i> mins = Arrays.asList(new Vector3i(), new Vector3i(1,1,1), new Vector3i(3, 4, 5));
        List<Vector3i> size = Arrays.asList(new Vector3i(1,1,1), new Vector3i(3,3,3), new Vector3i(8, 5, 2));
        List<Vector3i> expectedMax = Arrays.asList(new Vector3i(), new Vector3i(3,3,3), new Vector3i(10, 8, 6));
        for (int i = 0; i < mins.size(); ++i)
        {
            Region3i region = Region3i.createFromMinAndSize(mins.get(i), size.get(i));
            assertEquals(mins.get(i), region.min());
            assertEquals(size.get(i), region.size());
            assertEquals(expectedMax.get(i), region.max());
            assertFalse(region.isEmpty());
        }
    }

    @Test
    public void testCreateRegionWithMinMax()
    {
        List<Vector3i> mins = Arrays.asList(new Vector3i(), new Vector3i(1,1,1), new Vector3i(3,4,5));
        List<Vector3i> expectedSize = Arrays.asList(new Vector3i(1,1,1), new Vector3i(3,3,3), new Vector3i(8, 5, 2));
        List<Vector3i> max = Arrays.asList(new Vector3i(), new Vector3i(3,3,3), new Vector3i(10, 8, 6));
        for (int i = 0; i < mins.size(); ++i)
        {
            Region3i region = Region3i.createFromMinMax(mins.get(i), max.get(i));
            assertEquals(mins.get(i), region.min());
            assertEquals(max.get(i), region.max());
            assertEquals(expectedSize.get(i), region.size());
            assertFalse(region.isEmpty());
        }
    }

    @Test
    public void testCreateRegionWithBounds()
    {
        Region3i expectedRegion = Region3i.createFromMinMax(new Vector3i(-2,4,-16), new Vector3i(4, 107, 0));
        List<Vector3i> vec1 = Arrays.asList(new Vector3i(-2,4,-16), new Vector3i(4,4,-16), new Vector3i(-2,107,-16), new Vector3i(-2,4,0), new Vector3i(4,107,-16), new Vector3i(4,4,0), new Vector3i(-2,107,0), new Vector3i(4,107,0));
        List<Vector3i> vec2 = Arrays.asList(new Vector3i(4,107,0),  new Vector3i(-2,107,0), new Vector3i(4,4,0), new Vector3i(4,107,-16),  new Vector3i(-2,4,0),    new Vector3i(-2,107,-16), new Vector3i(4,4,-16), new Vector3i(-2,4,-16));
        for (int i = 0; i < vec1.size(); ++i)
        {
            assertEquals(expectedRegion, Region3i.createBounded(vec1.get(i), vec2.get(i)));
        }
    }

    @Test
    public void testRegionEmptyIfMaxLessThanMin()
    {
        Region3i region = Region3i.createFromMinMax(new Vector3i(0,0,0), new Vector3i(-1,0,0));
        assertTrue(region.isEmpty());
    }

    @Test
    public void testRegionEmptyIfSizeZeroOrLess()
    {
        Region3i region = Region3i.createFromMinAndSize(new Vector3i(1,1,1), new Vector3i(0,1,1));
        assertTrue(region.isEmpty());
        region = Region3i.createFromMinAndSize(new Vector3i(1,1,1),new Vector3i(1,-1,1));
        assertTrue(region.isEmpty());
    }

    @Test
    public void testIterateRegion()
    {
        Vector3i min = new Vector3i(2,5,7);
        Vector3i max = new Vector3i(10,11,12);
        Region3i region = Region3i.createFromMinMax(min, max);

        Set<Vector3i> expected = Sets.newHashSet();
        for (int x = min.x; x <= max.x; ++x)
        {
            for (int y = min.y; y <= max.y; ++y)
            {
                for (int z = min.z; z <= max.z; ++z)
                {
                    expected.add(new Vector3i(x,y,z));
                }
            }
        }

        for (Vector3i pos : region)
        {
            assertTrue(expected.contains(pos));
            expected.remove(pos);
        }

        assertEquals("All vectors provided", 0, expected.size());
    }

    @Test
    public void testSimpleIntersect()
    {
        Region3i region1 = Region3i.createFromMinMax(new Vector3i(), new Vector3i(32,32,32));
        Region3i region2 = Region3i.createFromMinMax(new Vector3i(1,1,1), new Vector3i(17,17,17));
        assertEquals(region2, region1.intersect(region2));
    }

    @Test
    public void testNonTouchingIntersect()
    {
        Region3i region1 = Region3i.createFromMinMax(new Vector3i(), new Vector3i(32,32,32));
        Region3i region2 = Region3i.createFromMinMax(new Vector3i(103,103,103), new Vector3i(170,170,170));
        assertEquals(new Region3i(), region1.intersect(region2));
    }

    @Test
    public void testEncompasses()
    {
        Region3i region = Region3i.createFromMinMax(new Vector3i(), new Vector3i(2,2,2));
        for (Vector3i v : region)
        {
            assertTrue(region.encompasses(v));
        }
        assertFalse(region.encompasses(new Vector3i(2,2,3)));
    }

    @Test
    public void testNearestPointToWhenEncompasses()
    {
        Region3i region = Region3i.createFromMinMax(new Vector3i(), new Vector3i(4,4,4));
        assertEquals(new Vector3i(2,1,1), region.getNearestPointTo(new Vector3i(2,1,1)));
    }

    @Test
    public void testNearestPointToAlongSide()
    {
        Region3i region = Region3i.createFromMinMax(new Vector3i(), new Vector3i(4,4,4));
        assertEquals(new Vector3i(4,2,1), region.getNearestPointTo(new Vector3i(15,2,1)));
    }

    @Test
    public void testNearestPointToAwayFromCorner()
    {
        Region3i region = Region3i.createFromMinMax(new Vector3i(), new Vector3i(4,4,4));
        assertEquals(new Vector3i(4,4,4), region.getNearestPointTo(new Vector3i(15,12,7)));
    }
}
