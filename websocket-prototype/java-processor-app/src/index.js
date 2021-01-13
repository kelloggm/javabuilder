import React from 'react';
import ReactDOM from 'react-dom';
import './index.css';

import JavaIde from './JavaIde';

const userId = Math.floor(Math.random() * Math.floor(999));

ReactDOM.render(
  <JavaIde userId = {userId} />,
  document.getElementById('root')
);
