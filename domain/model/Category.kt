package com.tutedude.ecommerce.domain.model

data class CategoryItem(
    val id: String,
    val name: String,
    val iconName: String = "category"
)

object Categories {
    val list = listOf(
        CategoryItem("all", "All"),
        CategoryItem("electronics", "Electronics"),
        CategoryItem("fashion", "Fashion"),
        CategoryItem("jewelery", "Jewelery"),
        CategoryItem("home", "Home & Living"),
        CategoryItem("books", "Books"),
        CategoryItem("sports", "Sports & Fitness")
    )
}
