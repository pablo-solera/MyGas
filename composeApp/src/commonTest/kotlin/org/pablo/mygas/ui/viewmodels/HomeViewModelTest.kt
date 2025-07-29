package org.pablo.mygas.ui.viewmodels

import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue

class HomeViewModelTest {

 private lateinit var viewModel: HomeViewModel

@BeforeTest
 fun setUp() {
  //viewModel = HomeViewModel()

 }

 @Test
 fun `Viewmodel state is empty`(){
  assertTrue { 1+1==2 }
 }
}