package com.kampplus.hava.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class NearestProvinceTest {

    @Test
    fun `coordinates inside a city resolve to that city`() {
        assertEquals("Ankara", nearestProvince(39.93, 32.86)?.name)
        assertEquals("İzmir", nearestProvince(38.42, 27.14)?.name)
        assertEquals("Trabzon", nearestProvince(41.00, 39.72)?.name)
    }

    @Test
    fun `nearby suburb resolves to the closest province center`() {
        // Kadıköy İstanbul il merkezine Kocaeli'den daha yakındır
        assertEquals("İstanbul", nearestProvince(40.99, 29.03)?.name)
    }

    @Test
    fun `locations outside Turkey do not resolve to a province`() {
        assertNull(nearestProvince(37.42, -122.08)) // Mountain View, emülatör varsayılanı
        assertNull(nearestProvince(48.85, 2.35)) // Paris
    }
}
