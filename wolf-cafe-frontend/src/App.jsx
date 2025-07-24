// src/App.jsx

import './App.css';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import HeaderComponent from './components/HeaderComponent';
import FooterComponent from './components/FooterComponent';
import HomepageComponent from './components/HomepageComponent';
import ListRecipesComponent from './components/ListRecipesComponent';
import ItemComponent from './components/ItemComponent';
import InventoryComponent from './components/InventoryComponent';
import AddIngredientComponent from './components/AddIngredientComponent';
import RecipeComponent from './components/RecipeComponent';
import EditRecipeComponent from './components/EditRecipeComponent';
import MakeRecipeComponent from './components/MakeRecipeComponent';
import RegisterComponent from './components/RegisterComponent';
import LoginComponent from './components/LoginComponent';
import ModifyTaxRateComponent from './components/ModifyTaxRateComponent';
import OrderConfirmationComponent from './components/OrderConfirmationComponent';
import CreateOrderComponent from './components/CreateOrderComponent';
import ViewOrdersComponent from './components/ViewOrdersComponent';
import OrderDetailsComponent from './components/OrderDetailsComponent';
import AdminRegisterComponent from './components/AdminRegisterComponent';
import ManageUserComponent from './components/ManageUserComponent';
import OrderHistoryComponent from './components/OrderHistoryComponent'
import HistoryDetailsComponent from './components/HistoryDetailsComponent'
import { isUserLoggedIn } from './services/AuthService';
import 'bootstrap/dist/css/bootstrap.min.css';
import AccessibilitySettingsComponent from './components/AccessibilitySettingsComponent';
import { useState } from 'react';

/**
 * Protected route component to ensure only authenticated users can access certain routes.
 */
function AuthenticatedRoute({ children }) {
  const isAuth = isUserLoggedIn();
  if (isAuth) {
    return children;
  }
  return <Navigate to="/" />;
}

function App() {
  const [showAccessibility, setShowAccessibility] = useState(false);
  const handleAccessibilityClose = () => setShowAccessibility(false);
  const handleAccessibilityShow = () => setShowAccessibility(true);

  return (
    <BrowserRouter>
      <div className="app-container">
        <HeaderComponent onAccessibilityShow={handleAccessibilityShow} />
        <div id="main-content" className="content-wrapper">
          <Routes>
		  <Route path='/' element={<LoginComponent />}></Route>
		  <Route path='/register' element={<RegisterComponent />}></Route>
		  <Route path='/login' element={<LoginComponent />}></Route>
		  <Route path='/home' element={<AuthenticatedRoute><HomepageComponent /></AuthenticatedRoute>}></Route>
		  <Route path='/recipes' element={<AuthenticatedRoute><ListRecipesComponent /></AuthenticatedRoute>}></Route>
		  <Route path='/inventory' element = {<AuthenticatedRoute><InventoryComponent /></AuthenticatedRoute>}></Route>
		  <Route path='/add-item' element={<AuthenticatedRoute><ItemComponent /></AuthenticatedRoute>}></Route>
		  <Route path='/add-recipe' element = {<AuthenticatedRoute><RecipeComponent /></AuthenticatedRoute>}></Route>
		  <Route path="/add-ingredient" element={<AuthenticatedRoute><AddIngredientComponent /></AuthenticatedRoute>}></Route>
		  <Route path='/update-item/:id' element={<AuthenticatedRoute><ItemComponent /></AuthenticatedRoute>}></Route>
		  <Route path="/edit-recipe/:name" element={<AuthenticatedRoute><EditRecipeComponent /></AuthenticatedRoute>}></Route>
		  <Route path='/make-recipe' element = {<AuthenticatedRoute><MakeRecipeComponent /></AuthenticatedRoute>}></Route>
		  <Route path='/create-account' element = {<AuthenticatedRoute><AdminRegisterComponent /></AuthenticatedRoute>}></Route>
		  <Route path='/manage-account' element = {<AuthenticatedRoute><ManageUserComponent /></AuthenticatedRoute>}></Route>
		  <Route path='/tax' element = {<AuthenticatedRoute><ModifyTaxRateComponent /></AuthenticatedRoute>}></Route>
		  <Route path='/orders' element = {<AuthenticatedRoute><ViewOrdersComponent /></AuthenticatedRoute>}></Route>
		  <Route path="/orders/:id" element = {<AuthenticatedRoute><OrderDetailsComponent /></AuthenticatedRoute>}></Route>
		  <Route path='/create-order' element = {<AuthenticatedRoute><CreateOrderComponent /></AuthenticatedRoute>}></Route>
		  <Route path='/order-confirmation/:id' element={<AuthenticatedRoute><OrderConfirmationComponent /></AuthenticatedRoute>} />
		  <Route path='/history' element = {<AuthenticatedRoute><OrderHistoryComponent /></AuthenticatedRoute>}></Route>
		  <Route path='/history/:id' element = {<AuthenticatedRoute><HistoryDetailsComponent /></AuthenticatedRoute>}></Route>
		  {/* Fallback route for unauthorized access */}
		  <Route path="/unauthorized" element={<div>Unauthorized Access</div>} />       
		  {/* Fallback route for 404 */}
		  <Route path="*" element={<div>404 Not Found</div>} />
          </Routes>
        </div>
        <FooterComponent />
        <AccessibilitySettingsComponent show={showAccessibility} handleClose={handleAccessibilityClose} />
      </div>
    </BrowserRouter>
  );
}

export default App;