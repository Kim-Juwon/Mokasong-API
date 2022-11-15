package com.mokasong.product.service;

import com.mokasong.common.dto.response.SuccessfulCreateResponse;
import com.mokasong.common.dto.response.SuccessfulResponse;
import com.mokasong.common.exception.custom.ConflictException;
import com.mokasong.common.exception.custom.NotFoundException;
import com.mokasong.common.exception.custom.PreconditionFailedException;
import com.mokasong.common.util.AwsS3Client;
import com.mokasong.product.dto.request.admin.CreateProductRequest;
import com.mokasong.product.dto.request.admin.UpdateProductRequest;
import com.mokasong.product.dto.response.admin.ProductResponse;
import com.mokasong.product.dto.response.admin.ProductsResponse;
import com.mokasong.product.entity.Product;
import com.mokasong.product.entity.ProductDetailCategory;
import com.mokasong.product.entity.ProductRootCategory;
import com.mokasong.product.query.admin.ProductsCondition;
import com.mokasong.product.repository.AdminCategoryMapper;
import com.mokasong.product.repository.AdminProductMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminProductServiceImplTest {
    @Mock AdminProductMapper adminProductMapper;
    @Mock AdminCategoryMapper adminCategoryMapper;
    @Mock AwsS3Client awsS3Client;
    AdminProductService service;

    @BeforeEach
    void init() {
        service = new AdminProductServiceImpl(adminProductMapper, adminCategoryMapper, awsS3Client);
    }

    @Nested
    class createProduct {
        CreateProductRequest request = new CreateProductRequest();
        List<MultipartFile> images = new ArrayList<>();

        @Nested
        @DisplayName("성공")
        class Success {

            @Test
            @DisplayName("성공 케이스")
            void test(@Mock MultipartFile image, @Mock ProductDetailCategory detailCategory) throws Exception {
                // given
                request.setName("테스트 상품");
                request.setDetailCategoryId(1L);
                request.setPrice(12000);
                request.setDiscountedPrice(null);
                request.setStock(3);
                images.add(image);


                // stubbing
                doReturn(detailCategory)
                        .when(adminCategoryMapper).getDetailCategory(request.getDetailCategoryId());
                doReturn("테스트 이미지.jpg")
                        .when(image).getOriginalFilename();
                doReturn("https://static.mokasong.com/images/products/example.png")
                        .when(awsS3Client).uploadFile("images/products", image);


                // when
                SuccessfulCreateResponse response = service.createProduct(request, images);


                // then
                assertTrue(response.getSuccess());
            }
        }

        @Nested
        @DisplayName("실패")
        class Fail {

            @Test
            @DisplayName("상세 카테고리 미존재")
            void detailCategoryNotFound(@Mock MultipartFile image) throws Exception {
                // given
                request.setName("테스트 상품");
                request.setDetailCategoryId(1L);
                request.setPrice(12000);
                request.setDiscountedPrice(null);
                request.setStock(3);
                images.add(image);


                // stubbing
                doReturn(null)
                        .when(adminCategoryMapper).getDetailCategory(request.getDetailCategoryId());


                // when & then
                Exception exception = assertThrows(NotFoundException.class, () -> {
                    service.createProduct(request, images);
                });

                assertEquals("선택한 상세 카테고리가 존재하지 않습니다.", exception.getMessage());
            }

            @Test
            @DisplayName("이미지 최대 개수 초과")
            void exceedMaximumCountOfImages(@Mock ProductDetailCategory detailCategory) throws Exception {
                // given
                request.setName("테스트 상품");
                request.setDetailCategoryId(1L);
                request.setPrice(12000);
                request.setDiscountedPrice(null);
                request.setStock(3);
                for (int i = 0; i < 21; i++) {
                    images.add(mock(MultipartFile.class));
                }


                // stubbing
                doReturn(detailCategory)
                        .when(adminCategoryMapper).getDetailCategory(request.getDetailCategoryId());


                // when & then
                Exception exception = assertThrows(PreconditionFailedException.class, () -> {
                    service.createProduct(request, images);
                });

                assertEquals("상품 1개당 이미지는 최대 20개입니다.", exception.getMessage());
            }
        }
    }

    @Nested
    class getProduct {
        Long productId;

        @Nested
        @DisplayName("성공")
        class Success {

            @Test
            @DisplayName("성공 케이스")
            void test(@Mock ProductResponse.AdminPageProduct product) throws Exception {
                // given
                productId = 1L;


                // stubbing
                doReturn(product)
                        .when(adminProductMapper).getProductForAdminPage(productId);


                // when
                ProductResponse response = service.getProduct(productId);


                // then
                assertNotNull(response.getProduct());
            }
        }

        @Nested
        @DisplayName("실패")
        class Fail {

            @Test
            @DisplayName("상품 조회 안됨")
            void productNotFound() throws Exception {
                // given
                productId = 1L;


                // stubbing
                doReturn(null)
                        .when(adminProductMapper).getProductForAdminPage(productId);


                // when & then
                Exception exception = assertThrows(NotFoundException.class, () -> {
                    service.getProduct(productId);
                });

                assertEquals("상품이 존재하지 않습니다.", exception.getMessage());
            }
        }
    }

    @Nested
    class getProducts {
        ProductsCondition condition = new ProductsCondition();

        @Nested
        @DisplayName("성공")
        class Success {

            @Test
            @DisplayName("limit, page만 존재")
            void test1() throws Exception {
                // given
                condition.setLimit(10L);
                condition.setPage(1L);


                // stubbing
                doReturn(48L)
                        .when(adminProductMapper).getTotalCountOfProductsByCondition(condition);
                doReturn(new ArrayList<ProductsResponse.Product>())
                        .when(adminProductMapper).getProductsByCondition(condition.extractBegin(), condition);


                // when
                ProductsResponse response = service.getProducts(condition);


                // then
                assertNotNull(response.getTotalCount());
                assertNotNull(response.getTotalPage());
                assertNotNull(response.getCurrentCount());
                assertNotNull(response.getCurrentPage());
                assertNotNull(response.getProducts());
            }

            @Test
            @DisplayName("rootCategoryId와 detailCategoryId 존재")
            void test2(@Mock ProductRootCategory rootCategory, @Mock ProductDetailCategory detailCategory) throws Exception {
                // given
                condition.setLimit(10L);
                condition.setPage(1L);
                condition.setRootCategoryId(1L);
                condition.setDetailCategoryId(1L);


                // stubbing
                doReturn(rootCategory)
                        .when(adminCategoryMapper).getRootCategory(condition.getRootCategoryId());
                doReturn(detailCategory)
                        .when(adminCategoryMapper).getDetailCategory(condition.getDetailCategoryId());
                doReturn(true)
                        .when(detailCategory).same(condition.getRootCategoryId());
                doReturn(48L)
                        .when(adminProductMapper).getTotalCountOfProductsByCondition(condition);
                doReturn(new ArrayList<ProductsResponse.Product>())
                        .when(adminProductMapper).getProductsByCondition(condition.extractBegin(), condition);


                // when
                ProductsResponse response = service.getProducts(condition);


                // then
                assertNotNull(response.getTotalCount());
                assertNotNull(response.getTotalPage());
                assertNotNull(response.getCurrentCount());
                assertNotNull(response.getCurrentPage());
                assertNotNull(response.getProducts());
            }
        }

        @Nested
        @DisplayName("실패")
        class Fail {

            @Test
            @DisplayName("최상위 카테고리 조회 안됨")
            void rootCategoryNotFound() throws Exception {
                // given
                condition.setLimit(10L);
                condition.setPage(1L);
                condition.setRootCategoryId(1L);


                // stubbing
                doReturn(null)
                        .when(adminCategoryMapper).getRootCategory(condition.getRootCategoryId());


                // when & then
                Exception exception = assertThrows(NotFoundException.class, () -> {
                    service.getProducts(condition);
                });

                assertEquals("선택한 최상위 카테고리가 존재하지 않습니다.", exception.getMessage());
            }

            @Test
            @DisplayName("상세 카테고리 조회 안됨")
            void detailCategoryNotFound(@Mock ProductRootCategory rootCategory) throws Exception {
                // given
                condition.setLimit(10L);
                condition.setPage(1L);
                condition.setRootCategoryId(1L);
                condition.setDetailCategoryId(1L);


                // stubbing
                doReturn(rootCategory)
                        .when(adminCategoryMapper).getRootCategory(condition.getRootCategoryId());
                doReturn(null)
                        .when(adminCategoryMapper).getDetailCategory(condition.getDetailCategoryId());


                // when & then
                Exception exception = assertThrows(NotFoundException.class, () -> {
                    service.getProducts(condition);
                });

                assertEquals("선택한 상세 카테고리가 존재하지 않습니다.", exception.getMessage());
            }
        }
    }

    @Nested
    class updateProduct {
        Long productId;
        UpdateProductRequest request = new UpdateProductRequest();
        List<MultipartFile> newImages = new ArrayList<>();

        @Nested
        @DisplayName("성공")
        class Success {

            @Test
            @DisplayName("성공 케이스")
            void test(@Mock Product product, @Mock ProductDetailCategory detailCategory, @Mock MultipartFile image) throws Exception {
                // given
                productId = 1L;
                request.setName("테스트 상품");
                request.setDetailCategoryId(2L);
                request.setPrice(14000);
                request.setDiscountedPrice(12000);
                request.setStock(2);
                newImages.add(image);


                // stubbing
                doReturn(product)
                        .when(adminProductMapper).getProduct(productId);
                doReturn(detailCategory)
                        .when(adminCategoryMapper).getDetailCategory(request.getDetailCategoryId());
                doReturn(true)
                        .when(product).needToUpdate(request);
                doReturn("테스트 이미지 2.png")
                        .when(image).getOriginalFilename();
                doReturn("https://static.mokasong.com/images/products/example2.png")
                        .when(awsS3Client).uploadFile("images/products", image);


                // when
                SuccessfulResponse response = service.updateProduct(productId, request, newImages);


                // then
                assertTrue(response.getSuccess());
            }
        }

        @Nested
        @DisplayName("실패")
        class Fail {

            @Test
            @DisplayName("상품 조회 안됨")
            void productNotFound() throws Exception {
                // given
                productId = 1L;
                request.setName("테스트 상품");
                request.setDetailCategoryId(2L);
                request.setPrice(14000);
                request.setDiscountedPrice(12000);
                request.setStock(2);


                // stubbing
                doReturn(null)
                        .when(adminProductMapper).getProduct(productId);


                // when & then
                Exception exception = assertThrows(NotFoundException.class, () -> {
                    service.updateProduct(productId, request, newImages);
                });

                assertEquals("상품이 존재하지 않습니다.", exception.getMessage());
            }

            @Test
            @DisplayName("상세 카테고리 조회 안됨")
            void detailCategoryNotFound(@Mock Product product) throws Exception {
                // given
                productId = 1L;
                request.setName("테스트 상품");
                request.setDetailCategoryId(2L);
                request.setPrice(14000);
                request.setDiscountedPrice(12000);
                request.setStock(2);


                // stubbing
                doReturn(product)
                        .when(adminProductMapper).getProduct(productId);
                doReturn(null)
                        .when(adminCategoryMapper).getDetailCategory(request.getDetailCategoryId());


                // when & then
                Exception exception = assertThrows(NotFoundException.class, () -> {
                    service.updateProduct(productId, request, newImages);
                });

                assertEquals("선택한 상세 카테고리가 존재하지 않습니다.", exception.getMessage());
            }

            @Test
            @DisplayName("이미지 20개 초과")
            void exceedMaximumCountOfImage(@Mock Product product, @Mock ProductDetailCategory detailCategory, @Mock MultipartFile image) throws Exception {
                // given
                productId = 1L;
                request.setName("테스트 상품");
                request.setDetailCategoryId(2L);
                request.setPrice(14000);
                request.setDiscountedPrice(12000);
                request.setStock(2);
                newImages.add(image);

                // stubbing
                doReturn(product)
                        .when(adminProductMapper).getProduct(productId);
                doReturn(detailCategory)
                        .when(adminCategoryMapper).getDetailCategory(request.getDetailCategoryId());

                List<MultipartFile> existingImages = new ArrayList<>();
                for (int i = 0; i < 20; i++) {
                    existingImages.add(mock(MultipartFile.class));
                }

                doReturn(existingImages)
                        .when(adminProductMapper).getImagesByProductId(productId);


                // when & then
                Exception exception = assertThrows(PreconditionFailedException.class, () -> {
                    service.updateProduct(productId, request, newImages);
                });

                assertEquals("상품 1개당 이미지는 최대 20개입니다.", exception.getMessage());
            }

            @Test
            @DisplayName("이미지 확장자가 png 또는 jpg가 아님")
            void invalidExtensionOfImage(@Mock Product product, @Mock ProductDetailCategory detailCategory, @Mock MultipartFile image) throws Exception {
                // given
                productId = 1L;
                request.setName("테스트 상품");
                request.setDetailCategoryId(2L);
                request.setPrice(14000);
                request.setDiscountedPrice(12000);
                request.setStock(2);
                newImages.add(image);


                // stubbing
                doReturn(product)
                        .when(adminProductMapper).getProduct(productId);
                doReturn(detailCategory)
                        .when(adminCategoryMapper).getDetailCategory(request.getDetailCategoryId());
                doReturn("테스트 이미지 2.gif")
                        .when(image).getOriginalFilename();


                // when & then
                Exception exception = assertThrows(PreconditionFailedException.class, () -> {
                    service.updateProduct(productId, request, newImages);
                });

                assertEquals("파일의 확장자가 png 또는 jpg(jpeg)여야 합니다.", exception.getMessage());
            }
        }
    }

    @Nested
    class deleteProduct {
        Long productId;

        @Nested
        @DisplayName("성공")
        class Success {

            @Test
            @DisplayName("성공 케이스")
            void test(@Mock Product product) throws Exception {
                // given
                productId = 1L;


                // stubbing
                doReturn(product)
                        .when(adminProductMapper).getProduct(productId);
                doReturn(false)
                        .when(product).getIsDeleted();


                // when
                SuccessfulResponse response = service.deleteProduct(productId);


                // then
                assertTrue(response.getSuccess());
            }
        }

        @Nested
        @DisplayName("실패")
        class Fail {

            @Test
            @DisplayName("상품 미존재")
            void productNotFound() throws Exception {
                // given
                productId = 1L;


                // stubbing
                doReturn(null)
                        .when(adminProductMapper).getProduct(productId);


                // when & then
                Exception exception = assertThrows(NotFoundException.class, () -> {
                    service.deleteProduct(productId);
                });

                assertEquals("상품이 존재하지 않습니다.", exception.getMessage());
            }

            @Test
            @DisplayName("이미 soft delete 되어있음")
            void alreadySoftDeleted(@Mock Product product) throws Exception {
                // given
                productId = 1L;


                // stubbing
                doReturn(product)
                        .when(adminProductMapper).getProduct(productId);
                doReturn(true)
                        .when(product).getIsDeleted();


                // when & then
                ConflictException exception = assertThrows(ConflictException.class, () -> {
                    service.deleteProduct(productId);
                });

                assertEquals("이미 soft delete 되어있는 상품입니다.", exception.getMessage());
            }
        }
    }

    @Nested
    class undeleteProduct {
        Long productId;

        @Nested
        @DisplayName("성공")
        class Success {

            @Test
            @DisplayName("성공 케이스")
            void test(@Mock Product product) throws Exception {
                // given
                productId = 1L;


                // stubbing
                doReturn(product)
                        .when(adminProductMapper).getProduct(productId);
                doReturn(true)
                        .when(product).getIsDeleted();


                // when
                SuccessfulResponse response = service.undeleteProduct(productId);


                // then
                assertTrue(response.getSuccess());
            }
        }

        @Nested
        @DisplayName("실패")
        class Fail {

            @Test
            @DisplayName("상품 미존재")
            void productNotFound() throws Exception {
                // given
                productId = 1L;


                // stubbing
                doReturn(null)
                        .when(adminProductMapper).getProduct(productId);


                // when & then
                Exception exception = assertThrows(NotFoundException.class, () -> {
                    service.undeleteProduct(productId);
                });

                assertEquals("상품이 존재하지 않습니다.", exception.getMessage());
            }

            @Test
            @DisplayName("이미 soft delete 되어있지 않음")
            void alreadyNotSoftDeleted(@Mock Product product) throws Exception {
                // given
                productId = 1L;


                // stubbing
                doReturn(product)
                        .when(adminProductMapper).getProduct(productId);
                doReturn(false)
                        .when(product).getIsDeleted();


                // when & then
                Exception exception = assertThrows(ConflictException.class, () -> {
                    service.undeleteProduct(productId);
                });

                assertEquals("soft delete 되어있는 상품이 아닙니다.", exception.getMessage());
            }
        }
    }
}