package com.example.product_order_service.product;

import com.example.product_order_service.ApiTest;
import com.example.product_order_service.product.adpater.ProductRepository;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;


import static org.assertj.core.api.Assertions.*;


class ProductApiTest extends ApiTest {

    @Autowired
    ProductRepository productRepository;


    @Test
    void 상품등록() {
        final var request = ProductSteps.상품등록요청_생성();
        final var response = ProductSteps.상품등록요청(request); // API 요청

        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @Test
    void 상품조회() {
        ProductSteps.상품등록요청(ProductSteps.상품등록요청_생성());
        Long productId = 1L;

        final var response = ProductSteps.상품조회요청(productId);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getString("name")).isEqualTo("상품명");
    }

    @Test
    void 상품수정() {
        ProductSteps.상품등록요청(ProductSteps.상품등록요청_생성());
        final long productId = 1L;

        final ExtractableResponse<Response> response = 상품수정요청(productId);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(productRepository.findById(1L).get().getName()).isEqualTo("상품 수정");

    }

    private ExtractableResponse<Response> 상품수정요청(long productId) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(ProductSteps.상품수정요청_생성())
                .when()
                .patch("/products/{productId}", productId)
                .then().log().all()
                .extract();
    }
}
