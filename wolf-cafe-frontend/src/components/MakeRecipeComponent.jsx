import React, { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { listRecipes } from '../services/RecipesService'
import { makeRecipe } from '../services/MakeRecipeService'

/** Provides functionality to make a recipe, pay for it, and receive change.*/
const ListRecipesComponent = () => {

    const [recipes, setRecipes] = useState([])
    const [amtPaid, setAmtPaid] = useState('')
    const [change, setChange] = useState(0)
	const [validationMessage, setValidationMessage] = useState('')
	const [validationColor, setValidationColor] = useState('')
    const [errors, setErrors] = useState( {
        general: ""
    })

    useEffect(() => {
        getAllRecipes()
    }, [])

    function getAllRecipes() {
        listRecipes().then((response) => {
            setRecipes(response.data)
        }).catch(error => {
            console.error(error)
        })
    }

    function craftRecipe(e, name, amtPaid) {
		e.preventDefault()
        console.log(name, amtPaid)
		setValidationMessage('')
		getAllRecipes()
		setAmtPaid(0)

		if (validateForm()) {
	        makeRecipe(name, amtPaid).then((response) => {
				
	            setChange(response.data)
				// clear all previous errors and set up success message
				setValidationMessage('Your order is ready!')
				setValidationColor('text-success')
				setErrors({ general: "" })
				
	        }).catch(error => {
	            console.error(error)
	            const errorsCopy = {... errors}
	            if (error.response.status == 409) {
	                errorsCopy.general = "Insufficient funds to pay."
					setChange(amtPaid)
	            } 
	            if (error.response.status == 400) {
	                errorsCopy.general = "Insufficient inventory."
					setChange(amtPaid)
	            }
	            setErrors(errorsCopy)
	        })
		}
    }

    function validateForm() {
        let valid = true
        const errorsCopy = {... errors}
		
		if (amtPaid == null || amtPaid == "") {
			errorsCopy.general = "Please enter valid payment."
			setChange(0)
			valid = false
		}

        if (amtPaid < 0) {
            errorsCopy.general = "Amount paid must be a positive integer."
			setChange(0)
			valid = false
        }

        setErrors(errorsCopy)
        return valid
    }

    function getGeneralErrors() {
        if (errors.general) {
            return <div className="p-3 mb-2 bg-danger text-white">{errors.general}</div>
        }
    }

    return (
        <div className="container">
            <h2 className="text-center">List of Recipes</h2>
            { getGeneralErrors() }
            <br /><br />
            <div className="card-body">
                <form>
                    <div className="form-group mb-2">
                        <label className="form-label">Amount Paid</label>
                        <input
                            type="number"
                            name="amtPaid"
                            placeholder="How much are you paying?"
                            value={amtPaid}
                            onChange={(e) => setAmtPaid(e.target.value)}
                            className={`form-control ${errors.general ? "is-invalid":""}`}
                        >
                        </input>
                        <label className="form-label">Change: {change}</label>
						{validationMessage && (
						    <div className={`form-text ${validationColor}`}>
						        {validationMessage}
						    </div>
						)}
                    </div>
                </form>
            </div>

            <table className="table table-striped table-bordered">
                <thead>
                    <tr>
                        <th>Recipe Name</th>
                        <th>Recipe Price</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    {
                        recipes.map(recipe => 
                        <tr key={recipe.id}>
                            <td>{recipe.name}</td>
                            <td>{recipe.price}</td>
                            <td>
                                <button className="btn btn-primary" onClick={(e) => craftRecipe(e, recipe.name, amtPaid)}
                                    style={{marginLeft: '10px'}}
                                >Make Recipe</button>
                            </td>
                        </tr>)
                    }
                </tbody>
            </table>
			
			<div className="d-flex justify-content-between align-items-center">
			    <Link to="/recipes" className="col-form-label" style={{ color: 'blue', cursor: 'pointer' }}>
			        Home
			    </Link>
			</div>
			
        </div>
    )

}

export default ListRecipesComponent