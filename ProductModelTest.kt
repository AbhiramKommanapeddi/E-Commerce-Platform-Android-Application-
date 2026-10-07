package com.tutedude.ecommerce

import com.tutedude.ecommerce.domain.model.Product
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ProductModelTest {

    @Test
    fun primaryImageUrl_returnsFirstImage_whenImagesExist() {
        val product = Product(
            id = "1",
            title = "Test Phone",
            price = 499.0,
            imageUrls = listOf("https://test.com/img1.jpg", "https://test.com/img2.jpg", "https://test.com/img3.jpg")
        )

        assertEquals("https://test.com/img1.jpg", product.primaryImageUrl)
        assertEquals(3, product.imageUrls.size)
    }

    @Test
    fun primaryImageUrl_returnsFallback_whenImagesEmpty() {
        val product = Product(
            id = "2",
            title = "Empty Images Product",
            imageUrls = emptyList()
        )

        assertTrue(product.primaryImageUrl.isNotBlank())
    }

    @Test
    fun defaultProduct_hasZeroPrice_andIsNotFavorite() {
        val product = Product()

        assertEquals(0.0, product.price, 0.001)
        assertFalse(product.isFavorite)
        assertFalse(product.isRecommended)
    }
}
