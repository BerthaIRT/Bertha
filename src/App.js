import React, { Component } from 'react';
import './App.css';
import ToolBar from './ToolBar.js';
import { Grid, Cell} from 'react-md'; 


class App extends Component {
  render() {
  

    return (
        <Grid>
          <Cell size={12} ><img src='Berta-website-god-image.png' alt="universityOfAl" height="125" width="150" class="fadeImage"/></Cell>
          
          <ToolBar></ToolBar>
        </Grid>
    );
  }
}



export default App;
